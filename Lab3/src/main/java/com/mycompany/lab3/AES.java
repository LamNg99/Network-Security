/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lab3;

/**
 *
 * @author lamnguyen
 */

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    
    private static SecretKeySpec secretKey;
    private static byte[] key;
     
    public static void setKey(final String myKey) {
        MessageDigest sha = null;
        try {
          key = myKey.getBytes("UTF-8");
          sha = MessageDigest.getInstance("SHA-1");
          key = sha.digest(key);
          key = Arrays.copyOf(key, 16);
          secretKey = new SecretKeySpec(key, "AES");
        } catch (Exception E) {
            System.err.println("Encrypt Exception : "+E.getMessage());
        }
    }
    public static String encrypt(String text, String key) {
        try {
            setKey(key);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(text.getBytes("UTF-8"));
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(cipherText);
            
        } catch (Exception E) {
             System.err.println("Encrypt Exception : "+E.getMessage());
        }
        
        return null;
    }
    
    public static String decrypt(String text, String key) {
        try {
            setKey(key);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] cipherText = decoder.decode(text.getBytes("UTF-8"));
            return new String(cipher.doFinal(cipherText));

        } catch (Exception E) {
            System.err.println("Decrypt Exception : "+E.getMessage());
        }
        return null;
    }
}