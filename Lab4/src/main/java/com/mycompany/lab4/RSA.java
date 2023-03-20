/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lab4;

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
import java.security.*;
import java.lang.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;

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
             System.err.println("RSA Encrypt Exception : "+E);
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
             System.err.println("RSA Decrypt Exception : "+E);
             //System.out.println(text);
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
             System.err.println("RSA Inverted Encrypt Exception : "+E);
        }
        
        return null;
    }

    public static String invertedEncryptChunks(String text, PrivateKey privateKey){
        byte[] fullData = null;
        String encrypted = "";
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            Base64.Encoder encoder = Base64.getEncoder();
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] textBytes = text.getBytes("UTF-8");

            int chunkSize = 245;
            int encryptSize = (int)(Math.ceil(text.length() / 245.0) * 256);
            int i = 0;
            ByteBuffer buffer = ByteBuffer.allocate(encryptSize);
            while(i < textBytes.length){
                int length = Math.min(text.length() - i, chunkSize);
                byte[] encChunk = cipher.doFinal(textBytes, i, length);
                buffer.put(encChunk);
                i += length;
            }
            fullData = buffer.array();
            encrypted = encoder.encodeToString(fullData);
        }catch (Exception E){
            System.err.println("RSA Inverted Chunk Encrypt Error " + E);
        }

        return encrypted;
    }
    
    public static String invertedDecrypt(String text, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decryptedBytes = decoder.decode(text.getBytes("UTF-8"));
            return new String(cipher.doFinal(decryptedBytes));         
        } catch (Exception E) {
             System.err.println("RSA Inverted Decrypt Exception : "+E);
        }
        
        return null;
    }

    public static String invertedDecryptChunks(String text, PublicKey publicKey){
        ByteBuffer buffer = null;
        byte[] decryptedBytes = null;
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] textBytes = text.getBytes();
            Base64.Decoder decoder = Base64.getDecoder();

            int chunkSize = 256;
            int i = 0;
            buffer = ByteBuffer.allocate(textBytes.length);

            while(i < textBytes.length){
                int length = Math.min(textBytes.length - i, chunkSize);
                byte[] chunk = cipher.doFinal(textBytes, i, length);
                buffer.put(chunk);
                i += length;
            }
            decryptedBytes = decoder.decode(buffer.array());
            return new String(cipher.doFinal(decryptedBytes));
        }catch(Exception E){
            System.err.println("RSA Inverted Chunk Decrypt Error " + E);
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

    public static String sha256(String msg) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(msg.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) 
                  hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
           throw new RuntimeException(ex);
        }
    }  
}