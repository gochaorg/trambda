package xyz.cofe.trambda.tcp;

import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import xyz.cofe.text.Text;
import xyz.cofe.trambda.log.api.Logger;

import static xyz.cofe.trambda.tcp.Hash.md5;

/**
 * Заголовок TCP пакета.
 *
 * <br>
 * Структура заголовка:
 *
 * <ul>
 *     <li>methodName = Имя метода : String, при том имя должно соответствовать:
 *     <ul>
 *         <li>Соответствовать регулярному выражению <code>(?i)[\w\d\-_.]+</code></li>
 *         <li>Минимальная длина 1</li>
 *         <li>Не содержать <code>\n \r \0</code></li>
 *     </ul>
 *     </li>
 *     <li>append = Есть есть payload (полезная нагрузка), тогда
 *     <ul>
 *         <li>';payload-size='+(10ное число - payloadSize в байтах)</li>
 *         <li>';payload-md5='+(hex значение MD5 хеша полезной нагрузки)</li>
 *     </ul>
 *     </li>
 *     <li>extra = Если есть дополнительные {@link HeaderValue} то они кодируются:
 *         <code>
 *             headerValues.map { hv -&gt; ';' + hv.getName() + '=' + hv.getValue() }
 *         </code>
 *     </li>
 * </ul>
 *
 * Указанные значения формируются в виде длинной строки:
 * <code>methodName + append + extra + '\n'</code>
 *
 * <br>
 * Потом данная строка кодируется в байты, кодировка ISO_8859_1
 *
 * <br>
 * Формально можно преставить пакет таким образом
 * 
 * <table border="1">
 * <tr>
 * <td rowspan="3">0 ... HeadSize</td>
 * <td colspan="2">Заголовок</td>
 * </tr>
 * 
 * <tr>
 * <td colspan="2">Кодированная строка в ISO_8859_1 с одим переводом строки '\n'.
 * <br>
 * Содержит:
 * </td>
 * </tr>
 * 
 * <tr>
 * <td>Имя метода/класса + пробел</td>
 * <td>
 * Пары ключ/значение разделенные точкой с запятой '<b>;</b>'
 * <p> Ключ от значения отделяются знаком равно '<b>=</b>'
 * <p>
 * Есть обязательные и опциональные значения:
 * <ul>
 * <li><b>payload-size</b> = размер полезной нагрузки</li>
 * <li><b>payload-md5</b> = md5 хеш полезной нагрузки</li>
 * <li><b>sid</b> = идентификатор сообщения</li>
 * <li><b>referrer</b> = на какое сообщение-запрос ссылается это сообщение-ответ</li>
 * </ul>
 * </td>
 * </tr>
 * 
 * <tr>
 * <td>HeadSize ... HeadSize + payload-size</td>
 * <td colspan="2">
 * Полезные данные, 
 * обычно это должно быть <a href="https://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html">сериализованное</a> представление
 * {@link Message}
 * </td>
 * </tr>
 * 
 * </table>
 */
public class TcpHeader {
    private static final Logger log = Logger.of(TcpHeader.class);

    private final int headerSize;
    private final String method;
    private final Map<String,String> values;

    /**
     * Конструктор
     * @param hsize размер заголовка
     * @param methName имя метода
     * @param values карта ключ/значение
     */
    public TcpHeader(int hsize, String methName, Map<String,String> values){
        if( methName==null )throw new IllegalArgumentException( "methName==null" );
        if( values==null )throw new IllegalArgumentException( "values==null" );

        this.values = Map.copyOf(values);
        this.method = methName;
        this.headerSize = hsize;

        var sz = 0;
        if( values.containsKey(PAYLOAD_SIZE) ){
            var str = values.get(PAYLOAD_SIZE);
            try {
                sz = Integer.parseInt(str);
            } catch( NumberFormatException ex ){
                log.error("can't parse value {}={}",PAYLOAD_SIZE,str);
                log.error("parse error",ex);
                sz = -1;
            }
        }
        payloadSize = sz;

        if( values.containsKey(PAYLOAD_MD5) ){
            var str = values.get(PAYLOAD_MD5);
            md5  = Text.decodeHex(str);
        }else {
            md5 = null;
        }
    }

    /**
     * Возвращает размер заголовка
     * @return размер заголовка
     */
    public int getHeaderSize(){ return headerSize; }

    /**
     * Возвращает название метода
     * @return название метода
     */
    public String getMethodName(){ return method; }

    /**
     * Возвращает карту ключ/значение связанных с данным заголовком
     * @return карта ключ/значение
     */
    public Map<String,String> getValues(){ return values; }

    /**
     * Возвращает идентификатор сообщения
     * @return идентификатор сообщения
     */
    public Optional<Integer> getSid(){
        return sid.parse(this);
    }

    /**
     * Возвращает идентификатор исходного сообщения, ответ на исходное сообщение
     * @return идентификатор на которое ссылается данное сообщение
     */
    public Optional<Integer> getReferrer(){
        return referrer.parse(this);
    }

    /**
     * Заголовок - идентификатор сообщения
     */
    public static final HeaderValue.IntValue sid = HeaderValue.IntValue.create("sid");

    /**
     * Заголовок - идентификатор исходного сообщения,
     * ответ на исходное сообщение
     */
    public static final HeaderValue.IntValue referrer = HeaderValue.IntValue.create("referrer");

    private final int payloadSize;
    public int getPayloadSize(){ return payloadSize; }

    private final byte[] md5;
    public Optional<byte[]> getPayloadMd5(){
        if( md5==null || md5.length<1 )return Optional.empty();
        return Optional.of(Arrays.copyOf(md5,md5.length));
    }

    /**
     * Сравнение полезной нагрузки с хеш значением указанным в заголовке
     * @param buff полезная нагрузка
     * @param off полезная нагрузка, смещение
     * @param len полезная нагрузка, размер
     * @return true - есть совпадение, false - нет совпадения
     */
    public boolean matched(byte[] buff,int off,int len){
        if( buff==null )throw new IllegalArgumentException( "buff==null" );
        if( off<0 )throw new IllegalArgumentException( "off<0" );
        if( len<0 )throw new IllegalArgumentException( "len<0" );
        if( len==0 ){
            if( md5==null||md5.length<1 )return true;
            throw new IllegalArgumentException( "len==0" );
        }
        if( (off+len)>buff.length )throw new IllegalArgumentException( "(off+len)>=buff.length" );

        if( md5==null||md5.length<1 )return true;
        var d1 = md5(buff,off,len);
        var d0 = md5;

        if( d0.length!=d1.length ){
            log.debug("hash length not matched, header md5 len {}, data md5 len {}",d0.length, d1.length);
            return false;
        }

        boolean m = true;
        for( int i=0;i<d0.length;i++ ){
            if(d0[i]!=d1[i]){
                m = false;
                break;
            }
        }

        if(!m){
            log.debug("hash not matched\nsrc :{}\ndata:{}",
                Text.encodeHex(d0),
                Text.encodeHex(d1)
            );
        }

        return m;
    }

    private static final String PAYLOAD_SIZE = "payload-size";
    private static final String PAYLOAD_MD5  = "payload-md5";

    private static long crc32(byte[] data,int off,int size){
        var chSum = new CRC32();
        chSum.update(data,off,size);
        return chSum.getValue();
    }

    private static final Pattern methodNamePattern = Pattern.compile("(?i)[\\w\\d\\-_.]+");
    private static final Pattern valuePattern = Pattern.compile("(?i)[\\w\\d\\-_./+=]+");

    /**
     * Кодировка заголовка
     * @param method метод
     * @param payload полезная нагрузка
     * @return байтовое представление
     */
    public static byte[] encode(String method,byte[] payload){
        return encode(method,payload);
    }

    /**
     * Кодировка заголовка
     * @param method метод
     * @param payload полезная нагрузка
     * @param values дополнительные опции заголовка
     * @return байтовое представление
     */
    @SafeVarargs
    public static byte[] encode(String method, byte[] payload, HeaderValue<? extends Object> ... values){
        LinkedHashMap<String,String> vals = new LinkedHashMap<>();
        if( values!=null ){
            for( var v : values ){
                if( v!=null ){
                    vals.put(v.getName(), v.asString());
                }
            }
        }
        return encode(method,payload,vals);
    }
    private static byte[] encode(String method,byte[] payload,Map<String,String> values){
        if( method == null ) throw new IllegalArgumentException("method==null");
        if( method.length() < 1 ) throw new IllegalArgumentException("method.length()<1");
        if( method.contains("\n") )throw new IllegalArgumentException( "method.contains(\""+"\\n"+"\")" );
        if( method.contains("\r") )throw new IllegalArgumentException( "method.contains(\""+"\\r"+"\")" );
        if( method.contains("\0") )throw new IllegalArgumentException( "method.contains(\""+"\\0"+"\")" );
        if( !methodNamePattern.matcher(method).matches() ){
            throw new IllegalArgumentException("method not matched with regex: "+methodNamePattern.pattern());
        }

        StringBuilder sb = new StringBuilder();
        sb.append(method);

        Set<String> keys = new HashSet<>();

        if( payload!=null && payload.length>0 ){
            sb.append(";").append(PAYLOAD_SIZE).append("=").append(payload.length);
            sb.append(";").append(PAYLOAD_MD5).append("=").append(
                Text.encodeHex(md5(payload,0,payload.length))
            );
            keys.add(PAYLOAD_SIZE);
            keys.add(PAYLOAD_MD5);
        }
        if( values!=null ){
            values.forEach((k,v)->{
                if( k==null )throw new IllegalArgumentException("header key is null");

                k = k.trim().toLowerCase();
                if( k.length()<1 )throw new IllegalArgumentException("header key is empty");
                if( keys.contains(k) )throw new IllegalArgumentException("header key '"+k+"' is added");
                if( !methodNamePattern.matcher(k).matches() )throw new IllegalArgumentException("header value key '"+k+"' not matched with regex: "+methodNamePattern.pattern());

                if( v==null || v.trim().length()<1 )
                    throw new IllegalArgumentException("header value '"+k+"' is empty or null");

                keys.add(k);
                sb.append(";").append(k).append("=").append(v);
            });
        }
        sb.append("\n");

        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    /**
     * Парсинг заголовка
     * @param buff байтовое представление заголовка
     * @param off смещение
     * @param len длина
     * @return Распознанный заголовок
     */
    public static Optional<TcpHeader> parse(byte[] buff, int off, int len){
        if( buff==null )throw new IllegalArgumentException( "buff==null" );
        if( off<0 )throw new IllegalArgumentException( "off<0" );
        if( len<0 )throw new IllegalArgumentException( "len<0" );
        if( len==0 )return Optional.empty();
        if( (off+len)>buff.length )throw new IllegalArgumentException( "(off+len)>=buff.length" );

        int foundEnd = -1;
        int foundMarkerLen = 1;
        for( int i=0; i<len; i++ ){
            int j = i+off;
            if( buff[j]==0 || ((int)buff[j])==(int)'\n' || ((int)buff[j])==(int)'\r' ){
                foundEnd = i;
                if( i<len-2 ){
                    char c0 = (char)((int)buff[j]);
                    char c1 = (char)((int)buff[j+1]);
                    if( c0=='\r' && c1=='\n' ){
                        foundMarkerLen = 2;
                    }
                }
                break;
            }
        }

        if( foundEnd<0 )return Optional.empty();

        String headerText = new String(buff,0,foundEnd, StandardCharsets.ISO_8859_1);
        String[] headers = headerText.split("\\s*;\\s*");
        if( headers.length<1 )return Optional.empty();

        int headerSize = foundEnd+foundMarkerLen;
        String methodName = headers[0];
        Map<String,String> values = new LinkedHashMap<>();

        for( int hn=1; hn<headers.length; hn++ ){
            String[] kv = headers[hn].split("\\s*=\\s*",2);
            if( kv.length==2 ){
                values.put(kv[0].toLowerCase().trim(), kv[1]);
            }
        }

        return Optional.of(new TcpHeader(headerSize,methodName,values));
    }
}
