/*
 * Copyright 2015 Enamul.
 *
 * Most of my softwares are open for educational purpose, but some are 
 * confidential. So, before using it openly drop me some lines at
 *
 *      enamsustcse@gmail.com
 *
 * I do not guarantee that the software would work properly. There could
 * remain bugs. If you found any of them, kindly report me.
 * If you need to use this or some part of it, use it at your own risk.
 * This software is not a professionally developed, so commercial use 
 * is not approved by default.
 */
package security;

import java.security.Key;
import java.util.HashMap;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class has static functions to encrypt and decrypt data against a key. 
 * @since 1.0
 * @version 1.0
 * @author Enamul
 */
public class Encryption {

    private static final String ALGO = "AES";
    static String IV = "AAAAAAAAAAAAAAAA";
    static HashMap<Character,Character> keys = new HashMap();
    /**
     * It gets the original data and a key to encrypt it. Then does the encryption.
     * @param Data The original data.
     * @param secretKey the secret key against which the encrypted data would be
     * produced.
     * @return encrypted string
     * @throws Exception if any unavoidable circumstances occurs.
     */
    public static String encrypt(String Data, String secretKey) throws Exception {
        //System.out.println(Data+"|========|"+secretKey);
        String ret = "";
        for(int i = 0; i<Data.length();i++){
            ret += (char)((char)65535 - Data.charAt(i));
        }
//        System.out.println("Enc: "+ret);
        return ret;
//        Key key = generateKey(secretKey);
//        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
//        cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
//        byte[] encVal = cipher.doFinal(Data.getBytes("UTF-8"));
//        String encryptedValue = new String(encVal,"UTF-8");
//        secretKey = Hashing.sha512Hex(secretKey);
//        int n = secretKey.length(), m = 0;
//        byte[] values = Data.getBytes("UTF-8"), keys = secretKey.getBytes("UTF-8");
//        System.out.println(values.length+"||1||"+secretKey.length());
//        for(int i = 0; i<values.length;i++){
//            values[i]+=keys[m];
//            
//            System.out.print(values[i]+":"+ keys[m++]+" ");
//            m%=n;
//        }
//        System.out.println("");
////        System.out.println(" = Key1, "+new String(values,"UTF-8"));
//        return new String(values,"UTF-8");
    }
    
    /**
     * It gets the encrypted data and a key to decrypt it. Then does the decryption.
     * @param encryptedData The encrypted data.
     * @param secretKey the secret key by which the data would be decrypted.
     * @return The original string.
     * @throws Exception if any unavoidable circumstances occurs.
     */
    public static String decrypt(String encryptedData, String secretKey) throws Exception {
        //System.out.println(encryptedData+"========"+secretKey);
        String ret = "";
//        System.out.println("Encrypted: "+encryptedData);
        for(int i = 0; i<encryptedData.length();i++){
            ret += (char)((char)65535 - encryptedData.charAt(i));
        }
//        System.out.println("Data: "+ret);
        return ret;
        //return encryptedData.substring(0, encryptedData.length() -  secretKey.length());
//        Key key = generateKey(secretKey);
//        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
//        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
//        String decryptedValue = new String(cipher.doFinal(encryptedData.getBytes("UTF-8")),"UTF-8");;
//        secretKey = Hashing.sha512Hex(secretKey);
//        int n = secretKey.length(), m = 0;
//        byte[] values = encryptedData.getBytes("UTF-8"), keys = secretKey.getBytes("UTF-8");
//        System.out.println(values.length+"||2||"+secretKey.length());
//        for(int i = 0; i<values.length;i++){
//            values[i]-=keys[m];
//            
//            System.out.print(values[i]+"::"+ keys[m++]+" ");
//            m%=n;
//        }
////        System.out.println(" = Key, "+new String(values,"UTF-8"));
//        return new String(values,"UTF-8");
        
        
//        return decryptedValue;
    }
    
    /**
     * Generates a secret key using the user given secret key.
     * @param keyValue the secret key provided by user.
     * @return another special secret key
     * @throws Exception if any unavoidable circumstances occurs.
     */
    private static Key generateKey(String secret) throws Exception {
        while(secret.length()<16) secret+=secret;
        byte[] keyValue = secret.substring(0, 16).getBytes("UTF-8");
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }
}
