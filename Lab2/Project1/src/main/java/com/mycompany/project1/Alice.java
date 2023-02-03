/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project1;

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
    public static void main(String[] args) throws IOException {
        
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
            String IDENTITY="Alice";
            String SECRET_KEY="secret_key";
            
            AES aes = new AES();
            
            // Generate nonce
            Random random = new Random();
            int nonce = random.nextInt();
            String nonceAlice = Integer.toString(nonce);
            
            // Send identity and a nonce to Bob
            out.println(IDENTITY + " " + nonceAlice);
         
            // Recieve and decrypt Bob's message
            fromBob = in.readLine();
            String[] parts = fromBob.split(" ");
            String encryptedMessage = parts[0];
            String nonceBob = parts[1];
            System.out.println("Received message: " + fromBob);
            System.out.println("Decrypted message: " + aes.decrypt(encryptedMessage, SECRET_KEY) + " " + nonceBob);
            
            // Send message to Bob
            out.println(aes.encrypt(IDENTITY + " " + nonceBob, SECRET_KEY));
            
      
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
