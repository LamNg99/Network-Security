/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lab3;

/**
 *
 * @author lamnguyen
 */

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class RSA {
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        KeyPair generatedKeyPair = keyGen.generateKeyPair();
        return generatedKeyPair;
    }
    
    public static String encrypt(String text, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes("UTF-8"));
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(encryptedBytes);
        } catch (Exception E) {
             System.err.println("Encrypt Exception : "+E.getMessage());
        }
        
        return null;
    }
    
    public static String decrypt(String text, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decryptedBytes = decoder.decode(text.getBytes("UTF-8"));
            return new String(cipher.doFinal(decryptedBytes));         
        } catch (Exception E) {
             System.err.println("Decrypt Exception : "+E.getMessage());
        }
        
        return null;
    }
    
    public static String invertedEncrypt(String text, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes("UTF-8"));
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(encryptedBytes);         
        } catch (Exception E) {
             System.err.println("Encrypt Exception : "+E.getMessage());
        }
        
        return null;
    }
    
    public static String invertedDecrypt(String text, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decryptedBytes = decoder.decode(text.getBytes("UTF-8"));
            return new String(cipher.doFinal(decryptedBytes));         
        } catch (Exception E) {
             System.err.println("Decrypt Exception : "+E.getMessage());
        }
        
        return null;
    }
    
    public static void savePublicKey(String dir, PublicKey publicKey, String name) throws FileNotFoundException, IOException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(dir + "/" + name + ".key");
	fos.write(x509EncodedKeySpec.getEncoded());
	fos.close();
        
    }
    
    public static PublicKey loadPublicKey(String dir, String name) 
                        throws FileNotFoundException, IOException, 
                        NoSuchAlgorithmException, InvalidKeySpecException {
        File filePublicKey = new File(dir + "/" + name + ".key");
	FileInputStream fis = new FileInputStream(dir + "/" + name + ".key");
	byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
	fis.read(encodedPublicKey);
	fis.close();
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
	PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        
        return publicKey;
    }
}
