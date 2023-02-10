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

public class Bob {
    public static void main(String[] args) throws IOException, Exception {
         
        if (args.length != 1) {
            System.err.println("Usage: java SiriServer <port number>");
            System.exit(1);
        }
 
        int portNumber = Integer.parseInt(args[0]);
 
        try ( 
            ServerSocket bobSocket = new ServerSocket(portNumber);
            Socket aliceSocket = bobSocket.accept();
            PrintWriter out =
                new PrintWriter(aliceSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(aliceSocket.getInputStream()));
        ) {
        
            String inputLine; 
            String outputLine;
            String IDENTITY="Bob";
            String dir="/Users/lamnguyen/Desktop/School/COE817/Lab2/Project2";
            
            RSA rsa = new RSA();
            
            // Generate public/private key pair 
            KeyPair pair = rsa.generateKeyPair();
            PublicKey puB = pair.getPublic();
            PrivateKey prB = pair.getPrivate();
            
            // Save public key to file 
            rsa.savePublicKey(dir, puB, IDENTITY);
            
            // Generate Bob nonce
            Random random = new Random();
            int nonce = random.nextInt();
            String nonceBob = Integer.toString(nonce);
            
            // Receive Alice's identity and nonce
            inputLine = in.readLine();
            String[] parts = inputLine.split(" ");
            System.out.println("Received message: " + inputLine );
            String nonceAlice = parts[1];
            
            // Load Alice public key
            PublicKey puA = rsa.loadPublicKey(dir, "Alice");
            
            // Encrypt message and send to Alice
            String encyptedMessage = rsa.invertedEncrypt(nonceAlice, prB);
            String encyptedMessage1 = encyptedMessage.substring(0, (encyptedMessage.length()/2));
            String encyptedMessage2 = encyptedMessage.substring(encyptedMessage.length()/2);
            String encryptedResponse1 = rsa.encrypt(encyptedMessage1, puA);
            String encryptedResponse2 = rsa.encrypt(encyptedMessage2, puA);
            out.println(encryptedResponse1 + " " + encryptedResponse2 + " " + nonceBob);
            
            // Decrypt message from Alice
            inputLine = in.readLine();
            String[] lastMessage = inputLine.split(" ");
            String decryptedMessage1 = rsa.decrypt(lastMessage[0], prB);
            String decryptedMessage2 = rsa.decrypt(lastMessage[1], prB);
            System.out.println("Received message: " + inputLine);
            System.out.println("Decrypted message: " + rsa.invertedDecrypt(decryptedMessage1 + decryptedMessage2, puA));
            

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
