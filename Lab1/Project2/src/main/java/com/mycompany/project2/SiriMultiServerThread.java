/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2;

/**
 *
 * @author lamnguyen
 */

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class SiriMultiServerThread extends Thread {
    private Socket socket = null;
    private static final ArrayList<String> users = new ArrayList<>();
 
    public SiriMultiServerThread(Socket socket) {
        super("SiriMultiServerThread");
        this.socket = socket;
    }
     
    public void run() {
 
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            
            String inputLine, outputLine;
            String key = "TMU";
            String username = null;
            
            VigenereCipher vc = new VigenereCipher();
            
            out.println(vc.encrypt("Please enter your username:", key));
            inputLine = in.readLine();
            username = vc.decrypt(inputLine, key);
            
            while(users.contains(username)) {
                out.println(vc.encrypt("Username is already existed, please choose another one:", key));
                inputLine = in.readLine();
                username = vc.decrypt(inputLine, key);
            }
            
            users.add(username);
            System.out.println(username + " connected to the server.");
            
            SiriProtocol srp = new SiriProtocol();
            outputLine = srp.processInput(null);
            out.println(vc.encrypt(outputLine, key));
 
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Encrypted message from Client " + username + ": " + inputLine);
                System.out.println("Decrypted message from Client " + username + ": " + vc.decrypt(inputLine, key));
                outputLine = srp.processInput(vc.decrypt(inputLine, key));
                out.println(vc.encrypt(outputLine, key));
                if (outputLine.equals("Bye!"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
