/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project1;

/**
 *
 * @author lamnguyen
 */

import java.net.*;
import java.io.*;
 
public class SiriServer {
    public static void main(String[] args) throws IOException {
         
        if (args.length != 1) {
            System.err.println("Usage: java SiriServer <port number>");
            System.exit(1);
        }
 
        int portNumber = Integer.parseInt(args[0]);
 
        try ( 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
         
            String inputLine, outputLine;
            String key = "TMU";
             
            // Initiate conversation with client
            SiriProtocol srp = new SiriProtocol();
            VigenereCipher vc = new VigenereCipher();
            outputLine = srp.processInput(null);
            out.println(vc.encrypt(outputLine, key));
 
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Encrypted message from Client: " + inputLine);
                System.out.println("Decrypted message from Client: " + vc.decrypt(inputLine, key));
                outputLine = srp.processInput(vc.decrypt(inputLine, key));
                out.println(vc.encrypt(outputLine, key));
                if (outputLine.equals("Bye!"))
                    break;
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
