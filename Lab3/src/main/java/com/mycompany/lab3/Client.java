/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lab3;

/**
 *
 * @author lamnguyen
 */

import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static void main(String[] args) throws IOException, Exception {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java Client <host name> <port number>");
            System.exit(1);
        }
 
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
 
        try (
            Socket kkSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
            
            String fromServer;
            String fromUser;
            String IDENTITY;
            String dir="/Users/lamnguyen/Desktop/School/COE817/Lab3";
            
            RSA rsa = new RSA();
            AES aes = new AES();
            
            // Generate public/private key pair
            KeyPair pair = rsa.generateKeyPair();
            PublicKey puC = pair.getPublic();
            PrivateKey prC = pair.getPrivate();
            
            // Generate nonce          
            Random random = new Random();
            int nonce = random.nextInt();
            String nonceC = Integer.toString(nonce);
 
            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                if (fromServer.equals("You have been connected to the KDC server!"))
                    break;
                 
                fromUser = stdIn.readLine();
                IDENTITY = fromUser;
                rsa.savePublicKey(dir, puC, IDENTITY);
                if (fromUser != null) {
                    out.println(fromUser);            
                }
            }
            
            // Receive Alice's identity and nonce
            fromServer = in.readLine();
            String message1 = rsa.decrypt(fromServer, prC);
            String[] parts = message1.split(" ");
            System.out.println("Received message: " + message1 );
            String identityK = parts[0];
            String nonceK = parts[1];
            
            // Load server public key
            PublicKey puK = rsa.loadPublicKey(dir, identityK);
            
            // Encrypt nonces and send to sever
            String message2 = nonceC + " " + nonceK;
            out.println(rsa.encrypt(message2, puK));
            
            // Recieve sever nonce
            fromServer = in.readLine();
            System.out.println("Received message: " + rsa.decrypt(fromServer, prC));
            
            // Receive and decrypt key 
            fromServer = in.readLine();
            String[] lastMessage = fromServer.split(" ");
            String decryptedMessage1 = rsa.decrypt(lastMessage[0], prC);
            String decryptedMessage2 = rsa.decrypt(lastMessage[1], prC);
            String keyC = decryptedMessage1 + decryptedMessage2;
            String secretKey = rsa.invertedDecrypt(keyC, puK);
            System.out.println("Received message: " + secretKey);
            
            System.out.println("****************** Key Distribution Session ******************");
            System.out.println("To request: <request> <clientID>");
            System.out.println("To getkey: <getkey>");
            System.out.println("To exit: <exit>");
            System.out.println("**************************************************************");
            
            // Send requests to server
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Receive meassge: " + fromServer);
                String[] responses = fromServer.split(" ");
                if (responses[0].equals("encryptedKey")) {
                    System.out.println("Decrypted key: " + aes.decrypt(responses[1], secretKey));
                }
                if (fromServer.equals("exit"))
                    break;
                 
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    // System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
            
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
