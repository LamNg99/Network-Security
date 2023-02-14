/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project3;

/**
 *
 * @author lamnguyen
 */

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.EncodedKeySpec;
import java.security.Signature;
import java.security.MessageDigest;
import java.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;


public class DigitalSignature {
     public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        KeyPair generatedKeyPair = keyGen.generateKeyPair();
        return generatedKeyPair;
    }
     
    public static String sign(String msg, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(msg.getBytes(StandardCharsets.UTF_8));
        byte[] signature = privateSignature.sign();
        
        return Base64.getEncoder().encodeToString(signature);
    }
    
    public static boolean verify(String msg, String signature, PublicKey publicKey) throws Exception{
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(msg.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        
        return publicSignature.verify(signatureBytes);
    }
     
    public static void savePublicKey(String dir, PublicKey publicKey, String name) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(dir + "/" + name + ".key");
	fos.write(publicKey.getEncoded());        
	fos.close();
    }
    
    public static PublicKey loadPublicKey(String dir, String name) 
                        throws FileNotFoundException, IOException, 
                        NoSuchAlgorithmException, InvalidKeySpecException {
        File publicKeyFile = new File(dir + "/" + name + ".key");
	byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey pub = keyFactory.generatePublic(publicKeySpec);

        return pub ;
    }
     
    public String genSHA256(String msg){
        String encoded = "";
        byte[] hash = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(msg.getBytes());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        
        return Base64.getEncoder().encodeToString(hash);
    }
}
