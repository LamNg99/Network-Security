/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project3;

/**
 *
 * @author lamnguyen
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Random;
import java.io.*;

public class Alice {
                
        public static void main(String[] args) throws IOException, Exception {
        
        if (args.length != 2) {
            System.err.println("Usage: java Client <host name> <port number>");
            System.exit(1);
        }
 
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        //Key Generation
        DigitalSignature ds = new DigitalSignature();
        KeyPair pair = ds.generateKeyPair();
        PublicKey puA = pair.getPublic();
        PrivateKey prA = pair.getPrivate();
        
        //change directory string according to specific machine
        String dir = "/Users/lamnguyen/Desktop/School/COE817/Lab2/project3";
        String secDir = "/Users/lamnguyen/Desktop/School/COE817/Lab2/project3/NonceChallenge.txt";
        ds.savePublicKey(dir, puA, "Alice");
        
        //to read security number from file
        BufferedReader secReader = new BufferedReader(new FileReader(secDir));
        
        //RNG
        SecureRandom rand = new SecureRandom();
        String securityNum = "";
        securityNum = secReader.readLine();
        secReader.close();
       
        try (
            Socket aliceSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(aliceSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(aliceSocket.getInputStream()));
            BufferedReader stdIn =
                new BufferedReader(
                    new InputStreamReader(System.in))         
        ){
           
           String userInput = "";
           while((userInput = stdIn.readLine()) != null){
               String signedMessage = ds.sign(userInput, prA);
               out.println(signedMessage + " " + userInput + " " + ds.genSHA256(securityNum));
               break;
           }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
