/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project3;

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
    public static void main(String[] args) throws IOException, Exception{
        if (args.length != 1) {
            System.err.println("Usage: java SiriServer <port number>");
            System.exit(1);
        }
 
        int portNumber = Integer.parseInt(args[0]);
        String dir = "/Users/lamnguyen/Desktop/School/COE817/Lab2/project3";
        String secDir = "/Users/lamnguyen/Desktop/School/COE817/Lab2/project3/NonceChallenge.txt";
        
        Writer wr = new FileWriter(secDir);
        BufferedReader secReader = new BufferedReader(new FileReader(secDir));
        File secFile = new File(secDir);
        
        //RNG
        String securityNum = "";
        int newNum = 0;
        SecureRandom rand = new SecureRandom();
        
        //if the program has never been initiated, write a new rand number. Else, there is a new one already made
        if(secFile.length() <= 0){
            newNum = rand.nextInt(2147483646);
            wr.write(String.valueOf(newNum));
            wr.flush();
        }
        
        securityNum = secReader.readLine();
        
        try ( 
            ServerSocket bobSocket = new ServerSocket(portNumber);
            Socket aliceSocket = bobSocket.accept();
            PrintWriter out =
                new PrintWriter(aliceSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(aliceSocket.getInputStream()));
        ) {
            String inputLine = "";
            DigitalSignature ds = new DigitalSignature();
            PublicKey puA = ds.loadPublicKey(dir, "Alice");
            
            while ((inputLine = in.readLine()) != null) {
                String[] parts = inputLine.split(" ");
                //signature
                String signaturePart = parts[0];
                //message
                String plainTextMessage = parts[1];
                //security number
                String secNumStr = parts[2];
                boolean secPassed = false;
                
                //received signature
                System.out.println("The signed message is: " + signaturePart);
                if(ds.genSHA256(securityNum).equals(secNumStr)){
                    System.out.println("security test passed, proceeding to authentication/signature check \n");
                    secPassed = true;
                }else{
                    System.out.println("security test has failed, try resending");
                }
                
                //create a new security challenge
                PrintWriter pw = new PrintWriter(secDir);
                pw.close();

                int newSecChallenge = rand.nextInt();
                wr.write(String.valueOf(newSecChallenge));
                wr.close();
                
                if(!secPassed){
                    break;
                }
                System.out.println("Message Authenticated: " + ds.verify(plainTextMessage, signaturePart, puA));
            }  
        }catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection\n\n" + e );
            System.out.println(e.getMessage());
        }
    }
}
