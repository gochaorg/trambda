package xyz.cofe.trambda.tcp.ssl;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import xyz.cofe.text.BytesDump;
import xyz.cofe.trambda.tcp.T;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SSLTest {
    public static SSLContext createSSLContext(){
        try {
            // имя сертификата для использования
            String keyStoreFile = "/home/uzer/code/trambda/trambda-tcp/src/test/resources/SSLKey";

            // пароль сертификата
            char[] keyStorePass = "123456".toCharArray();

            // Основной пароль, используемый для псевдонима сертификата
            char[] keyPassword = "123456".toCharArray();

            // Создать хранилище ключей JKS
            KeyStore ks = null;
            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keyStoreFile),keyStorePass);

            // Создаем менеджер ключей X.509, который управляет хранилищем ключей JKS
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keyPassword);

            // Создайте среду SSL и укажите версию SSL 3.0. Вы также можете использовать TLSv1, но чаще используется SSLv3.
            SSLContext sslContext =
                //SSLContext.getInstance("SSLv3");
                SSLContext.getInstance("TLSv1");

            TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    System.out.println("checkClientTrusted auth type "+authType);
                    if( chain!=null ){
                        for( var cert : chain ){
                            if( cert!=null ){
                                System.out.println("  cert "+cert);
                            }
                        }
                    }
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    System.out.println("checkServerTrusted auth type "+authType);
                    if( chain!=null ){
                        for( var cert : chain ){
                            if( cert!=null ){
                                System.out.println("  cert "+cert);
                            }
                        }
                    }
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            /*
             * Инициализация среды SSL. Второй параметр - указать источник доверенного сертификата, используемого JSSE,
             * Установите значение null, чтобы получить сертификат от javax.net.ssl.trustStore.
             * Третий параметр - это случайное число, сгенерированное JSSE, этот параметр повлияет на безопасность системы,
             * Установка на ноль - хороший выбор, может гарантировать безопасность JSSE.
             */
            sslContext.init(kmf.getKeyManagers(), new TrustManager[]{ tm }, null);

            return sslContext;
        } catch (   KeyStoreException | UnrecoverableKeyException |
                    IOException | CertificateException | KeyManagementException |
                    NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }
    public static SSLContext defaultSSLContext(){
        try {
            return SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }

    public static class Server{
        protected Supplier<SSLContext> sslContext;
        public Server(){
            sslContext = SSLTest::defaultSSLContext;
        }
        public Server(Supplier<SSLContext> ctx){
            sslContext = ctx!=null ? ctx : SSLTest::defaultSSLContext;
        }

        protected SSLServerSocket createSSLServerSocket(int thePort) {
            var ctx = sslContext.get();

            ServerSocketFactory sslserversocketfactory =
                ctx.getServerSocketFactory();

            try {
                return (SSLServerSocket) sslserversocketfactory.createServerSocket(thePort);
            } catch (IOException e) {
                throw new Error(e);
            }
        }

        public final Collection<Session> sessions = new CopyOnWriteArrayList<>();
        private final AtomicBoolean stop = new AtomicBoolean(false);
        private final AtomicReference<Thread> runServerThread = new AtomicReference<>();

        public void stopServer(boolean waitForStop){
            this.stop.set(true);
            this.sessions.forEach(s -> s.stop(waitForStop));

            if( waitForStop ){
                var th = runServerThread.get();
                if( th==null || !th.isAlive() )return;
                if( Thread.currentThread().getId()==th.getId() ){
                    throw new IllegalThreadStateException("can't wait in same thread");
                }
                long t0=System.currentTimeMillis();
                while (th.isAlive()){
                    long td = System.currentTimeMillis() - t0;
                    th.interrupt();
                    if( td>1000*5L ){
                        th.stop();
                    }
                }
            }
        }
        public void startServer(int port ){
            var th = runServerThread.get();
            if( th!=null )throw new IllegalThreadStateException("all ready created");
            runServerThread.set(Thread.currentThread());

            System.out.println("create ssl server socket");
            try {
                var serverSocket = createSSLServerSocket(port);
                    serverSocket.setSoTimeout(1000);

                System.out.println("run server cycle");
                while(!stop.get()){
                    try {
                        SSLSocket client = (SSLSocket) serverSocket.accept();
                        var ses = new Session(client,this);
                        sessions.add(ses);
                    } catch ( SocketTimeoutException to ){
                        System.out.println("socket timeout");
                    } catch (IOException e){
                        System.out.println(e);
                        break;
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }

            System.out.println("server cycle finish");
        }
    }

    public static class Session {
        public final SSLSocket socket;
        public final Server server;
        public final Thread thread;

        public Session(SSLSocket socket, Server server){
            if( socket==null )throw new IllegalArgumentException("socket == null");
            if( server==null )throw new IllegalArgumentException("server == null");
            this.socket = socket;
            this.server = server;

            try {
                this.socket.setSoTimeout(1000);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            System.out.println("created session "+socket.getRemoteSocketAddress());
            thread = new Thread(this::runSession);
            thread.setDaemon(true);
            thread.start();
        }

        private final AtomicBoolean stopSignal = new AtomicBoolean(false);
        public void runSession(){
            System.out.println("run session");
            byte[] buff = new byte[1024];

            ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();

            while (!stopSignal.get()) {
                try {
                    int readed = socket.getInputStream().read(buff);
                    if( readed>0 ){
                        msgBytes.write(buff,0,readed);
                        System.out.println("readed "+readed+" msg "+msgBytes.size());
                        while ( msgBytes.size()>=4 ){
                            byte[] bytes = msgBytes.toByteArray();
                            System.out.println("buffer data ");
                            System.out.println(new BytesDump().dump(bytes));

                            int msgSize = IntCodec.fromBytes(bytes);
                            System.out.println("message size expect "+msgSize);

                            if( msgBytes.size()>=msgSize ){
                                System.out.println("buffer data accepted message");
                                decode(bytes,4,msgSize);
                            }

                            int truncSize = 4 + msgSize;
                            if( bytes.length>truncSize ){
                                var tailBytes = new byte[bytes.length - truncSize];
                                System.arraycopy(bytes,truncSize,tailBytes,0,tailBytes.length);
                                msgBytes = new ByteArrayOutputStream();
                                msgBytes.write(tailBytes);
                            }else {
                                msgBytes = new ByteArrayOutputStream();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        protected void decode( byte[] bytes, int from, int len ){
            var msg = new String(bytes,from,len,StandardCharsets.UTF_8);
            System.out.println("decode() msg = "+msg);
        }
        public void stop(boolean wait){
            stopSignal.set(true);
            long t0=System.currentTimeMillis();
            if( wait ){
                while (thread.isAlive()){
                    thread.interrupt();
                    long td = System.currentTimeMillis()-t0;
                    if( td>=5000 ){
                        thread.stop();
                    }
                }
            }
        }
    }
    public static class Client {
        public final String host;
        public final int port;
        protected final Supplier<SSLContext> sslContext;
        public Client(String host,int port){
            this.host = host;
            this.port = port;
            sslContext = SSLTest::defaultSSLContext;
        }
        public Client(String host,int port, Supplier<SSLContext> ctx){
            this.host = host;
            this.port = port;
            sslContext = ctx!=null ? ctx : SSLTest::defaultSSLContext;
        }

        public SSLSocketFactory sslFactory(){
            return sslContext.get().getSocketFactory();
        }
        public void run(){
            try {
                Socket s = sslFactory().createSocket(host, port);
                //System.out.println("socket "+s);
                if( s instanceof SSLSocket ){
                    var sslSock = ((SSLSocket)s);
                    //sslSock.setEnabledCipherSuites();
                    sslSock.setEnabledProtocols(new String[]{
                        //"TLSv1", "TLSv1.1", "TLSv1.2"
                        "TLSv1.3",
                        "TLSv1.2",
                        "SSLv2Hello"
                    });
                    sslSock.setEnabledCipherSuites(new String[]{
                        "TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                        "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_RSA_WITH_AES_256_GCM_SHA384",
                        "TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384",
                        "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                        "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256",
                        "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                        "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLS_RSA_WITH_AES_256_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384",
                        "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
                        "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA",
                        "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                        "TLS_DHE_DSS_WITH_AES_256_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                        "TLS_RSA_WITH_AES_128_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256",
                        "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                        "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA",
                        "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                        "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
                    });
                    sslSock.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                        @Override
                        public void handshakeCompleted(HandshakeCompletedEvent event) {
                            System.out.println("handshake complete");
                            System.out.println("  cipher suite "+event.getCipherSuite());
                            try {
                                var i = -1;
                                for( var pc : event.getPeerCertificates() ){
                                    i++;
                                    System.out.println("  peer cert "+i+
                                        " type="+pc.getType()+
                                        " pub.key.c="+pc.getPublicKey().getClass()+
                                        " pub.key="+pc.getPublicKey()
                                    );
                                }
                            } catch (SSLPeerUnverifiedException e) {
                                System.out.println("can't getPeerCertificates: "+e);
                            }
                        }
                    });
                }

                String msg = "hello";
                byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
                byte[] msgLen = IntCodec.toBytes(msgBytes.length);
                byte[] wbytes = Bytes.join(msgLen,msgBytes);

                System.out.println("send message, size="+wbytes.length);
                System.out.println(new BytesDump.Builder().build().dump(wbytes));

                s.getOutputStream().write(wbytes);
//                s.getOutputStream().write(msgLen);
//                s.getOutputStream().write(msgBytes);
                s.getOutputStream().flush();

                //s.shutdownOutput();
                //s.shutdownInput();
                s.close();
            } catch (IOException e) {
                System.out.println("client error");
                e.printStackTrace();
            }
        }
    }

    @Tag(T.SSL)
    @Test
    public void test01(){
        //System.getProperties().setProperty("javax.net.ssl.trustStore","/home/uzer/code/trambda/trambda-tcp/src/test/resources/SSLKey");
        System.getProperties().setProperty("javax.net.ssl.keyStore","/home/uzer/code/trambda/trambda-tcp/src/test/resources/testStore-2");
        System.getProperties().setProperty("javax.net.ssl.keyStorePassword","123456");

        System.getProperties().setProperty("javax.net.ssl.trustStore","/home/uzer/code/trambda/trambda-tcp/src/test/resources/testStore-2");
        System.getProperties().setProperty("javax.net.ssl.trustStorePassword","123456");

        Server server = new Server();

        Thread serverThread = new Thread(()->{
            server.startServer(9011);
        });
        serverThread.setDaemon(true);
        serverThread.start();

        System.out.println("wait 2 sec");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Client client = new Client("127.0.0.1",9011);
        client.run();

        long waitSec = 10;
        System.out.println("wait "+waitSec+" sec");
        try {
            Thread.sleep(waitSec*1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.stopServer(true);
        long activeSessionsCount = server.sessions.stream().map(s -> s.thread.isAlive()).filter(s -> s).count();
        System.out.println("active session count = "+activeSessionsCount);
    }

    @Tag(T.SSL)
    @Test
    public void test02(){
//        System.getProperties().setProperty("javax.net.ssl.keyStore","/home/uzer/code/trambda/trambda-tcp/src/test/resources/testStore-2");
//        System.getProperties().setProperty("javax.net.ssl.keyStorePassword","123456");
//
//        System.getProperties().setProperty("javax.net.ssl.trustStore","/home/uzer/code/trambda/trambda-tcp/src/test/resources/testStore-2");
//        System.getProperties().setProperty("javax.net.ssl.trustStorePassword","123456");

//        System.getProperties().setProperty("javax.net.debug","ssl:handshake");

        Server server = new Server(SSLTest::createSSLContext);

        Thread serverThread = new Thread(()->{
            server.startServer(9011);
        });
        serverThread.setDaemon(true);
        serverThread.start();

        System.out.println("wait 2 sec");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Client client = new Client("127.0.0.1",9011,SSLTest::createSSLContext);
        client.run();

        long waitSec = 10;
        System.out.println("wait "+waitSec+" sec");
        try {
            Thread.sleep(waitSec*1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.stopServer(true);
        long activeSessionsCount = server.sessions.stream().map(s -> s.thread.isAlive()).filter(s -> s).count();
        System.out.println("active session count = "+activeSessionsCount);
    }
}
