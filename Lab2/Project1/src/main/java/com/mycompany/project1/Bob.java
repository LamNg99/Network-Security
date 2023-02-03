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

public class Bob {
    public static void main(String[] args) throws IOException {
         
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
            String SECRET_KEY="secret_key";
            
            AES aes = new AES();
            
            // Generate nonce
            Random random = new Random();
            int nonce = random.nextInt();
            String nonceBob = Integer.toString(nonce);
            
            // Receive Alice's identity and nonce
            inputLine = in.readLine();
            String[] parts = inputLine.split(" ");
            System.out.println("Received message: " + inputLine );
            String nonceAlice = parts[1];
            
            // Encrypt message and send to Alice
            String response = IDENTITY + " " + nonceAlice; 
            out.println(aes.encrypt(response, SECRET_KEY) + " " + nonce);
            
            // Decrypt message from Alice
            inputLine = in.readLine();
            System.out.println("Received message: " + inputLine);
            System.out.println("Decrypted message: " + aes.decrypt(inputLine, SECRET_KEY));

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
