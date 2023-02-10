/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2;

/**
 *
 * @author lamnguyen
 */

import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.util.Random;

public class Alice {
    public static void main(String[] args) throws IOException, Exception {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java Client <host name> <port number>");
            System.exit(1);
        }
 
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
 
        try (
            Socket aliceSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(aliceSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(aliceSocket.getInputStream()));
        ) {
            
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
            
            String fromBob;
            String fromAlice;
            String IDENTITY = "Alice";
            String dir="/Users/lamnguyen/Desktop/School/COE817/Lab2/Project2";
            
            RSA rsa = new RSA();
            
            // Generate public/private key pair
            KeyPair pair = rsa.generateKeyPair();
            PublicKey puA = pair.getPublic();
            PrivateKey prA = pair.getPrivate();
            
            // Save public key to file 
            rsa.savePublicKey(dir, puA, IDENTITY);
          
            
            // Generate Alice nonce          
            Random random = new Random();
            int nonce = random.nextInt();
            String nonceAlice = Integer.toString(nonce);
            
            // Send identity and a nonce to Bob
            out.println(IDENTITY + " " + nonceAlice);
         
            // Recieve and decrypt Bob's message
            fromBob = in.readLine();
            String[] parts = fromBob.split(" ");
            String encryptedMessage1 = parts[0];
            String encryptedMessage2 = parts[1];
            String nonceBob = parts[2];
            String decryptedMessage1 = rsa.decrypt(encryptedMessage1, prA);
            String decryptedMessage2 = rsa.decrypt(encryptedMessage2, prA);
            
            // Load Bob public key
            PublicKey puB = rsa.loadPublicKey(dir, "Bob");

            System.out.println("Received message: " + fromBob);
            System.out.println("Decrypted message: " + rsa.invertedDecrypt(decryptedMessage1 + decryptedMessage2, puB) + " " + nonceBob);
            
            // Encrypt message and send to Bob
            String receivedMessage = rsa.invertedEncrypt(nonceBob, prA);
            String receivedMessage1 = receivedMessage.substring(0, (receivedMessage.length()/2));
            String receivedMessage2 = receivedMessage.substring(receivedMessage.length()/2);
            String encryptedResponse1 = rsa.encrypt(receivedMessage1, puB);
            String encryptedResponse2 = rsa.encrypt(receivedMessage2, puB);
            out.println(encryptedResponse1 + " " + encryptedResponse2);
                
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
    }
}
