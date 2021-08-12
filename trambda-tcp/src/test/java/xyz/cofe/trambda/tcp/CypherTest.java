package xyz.cofe.trambda.tcp;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import xyz.cofe.text.BytesDump;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class CypherTest {
    @Test
    public void test01(){
        Arrays.stream(Security.getProviders()).forEach( p -> {
            System.out.println(p.getName());
            System.out.println(p.getInfo());
            System.out.println(p);

            p.getServices().forEach(s -> {
                System.out.println("  service type="+s.getType()+" alg="+s.getAlgorithm()+" class="+s.getClassName()+" "+s);
            });
        });
    }

    /*
    byte[] keyBytes   = new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    String algorithm  = "RawBytes";
    SecretKeySpec key = new SecretKeySpec(keyBytes, algorithm);

    cipher.init(Cipher.ENCRYPT_MODE, key);
     */

    /**
     * Тест шифрования
     * @param transformation алгоритм
     * @param ivOpt
     */
    public void cipher(String transformation, Optional<IvParameterSpec> ivOpt){
        String text = "secret!!secret!!secret!!secret!!";

        try{
            // Generate new key
            KeyGenerator keygen = null;
            keygen = KeyGenerator.getInstance("AES");
            keygen.init(256);

            Key key = keygen.generateKey();

            // Encrypt with key
            //String transformation = "AES/ECB/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(transformation);
            ivOpt.ifPresentOrElse( iv -> {
                try{
                    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                } catch( InvalidKeyException | InvalidAlgorithmParameterException e ) {
                    e.printStackTrace();
                }
            }, ()->{
                try{
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                } catch( InvalidKeyException e ) {
                    e.printStackTrace();
                }
            });

            byte[] sourceData = text.getBytes();
            byte[] encrypted = cipher.doFinal(text.getBytes());

            System.out.println("sourceData ("+sourceData.length+")");
            System.out.println(new BytesDump.Builder().build().dump(sourceData));

            System.out.println("encrypted ("+encrypted.length+")");
            System.out.println(new BytesDump.Builder().build().dump(encrypted));

            // Decrypt with key
            ivOpt.ifPresentOrElse( iv -> {
                try{
                    cipher.init(Cipher.DECRYPT_MODE, key, iv);
                } catch( InvalidKeyException | InvalidAlgorithmParameterException e ) {
                    e.printStackTrace();
                }
            }, ()->{
                try{
                    cipher.init(Cipher.DECRYPT_MODE, key);
                } catch( InvalidKeyException e ) {
                    e.printStackTrace();
                }
            });

            byte[] decrypted = cipher.doFinal(encrypted);
            System.out.println("decrypted");
            System.out.println(new BytesDump.Builder().build().dump(decrypted));

            String result = new String(decrypted);
            System.out.println(result);
        } catch( NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void cipher01(){
        cipher("AES/ECB/PKCS5Padding", Optional.empty());
        System.out.println("- ".repeat(40));

        SecureRandom random = null;
        try{
            random = SecureRandom.getInstanceStrong();
            byte[] rnd = new byte[16];
            random.nextBytes(rnd);
            IvParameterSpec ivSpec = new IvParameterSpec(rnd);

            cipher("AES/CBC/PKCS5Padding", Optional.of(ivSpec));
        } catch( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
    }
}
