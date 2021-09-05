package xyz.cofe.trambda.tcp.ssl;

import org.junit.jupiter.api.Test;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class KeyStoreTest {
    @Test
    public void test01(){
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

            for( var km : kmf.getKeyManagers() ){
                System.out.println("km "+km);
            }

            System.out.println("KeyStore");
            var alEnum = ks.aliases();
            while (alEnum.hasMoreElements()){
                var alias = alEnum.nextElement();
                System.out.println("  alias "+alias);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
