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
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KDCServerThread extends Thread {
    private Socket socket = null;
    private static final ArrayList<String> users = new ArrayList<>();
    private static final HashMap<String, String> requests = new HashMap<>();
 
    public KDCServerThread(Socket socket) {
        super("KDCServerThread");
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
            String serverkey = "super_secret_key";
            String IDENTITY = "KDCServer";
            String dir="/Users/lamnguyen/Desktop/School/COE817/Lab3";
            String username = null;
            String key;
            String value;
            
            RSA rsa = new RSA();
            AES aes = new AES();
            
            // Generate public/private key pair
            KeyPair pair = rsa.generateKeyPair();
            PublicKey puK = pair.getPublic();
            PrivateKey prK = pair.getPrivate();
            
            // Save public key to file 
            rsa.savePublicKey(dir, puK, IDENTITY);
            
            // Generate nonce          
            Random random = new Random();
            int nonce = random.nextInt();
            String nonceK = Integer.toString(nonce);
            
            out.println("Please enter your username:");
            inputLine = in.readLine();
            username = inputLine;
            
            while(users.contains(username)) {
                out.println("Username is already existed, please choose another one:");
                inputLine = in.readLine();
                username = inputLine;
            }
            
            users.add(username);
            System.out.println(username + " connected to the server.");
            out.println("You have been connected to the KDC server!");
            
            // Load client public key
            PublicKey puC = rsa.loadPublicKey(dir, username);
            
            // Send identity and a nonce to client       
            String message1 = rsa.encrypt(IDENTITY + " " + nonceK, puC);
            out.println(message1);
            
            // Check nonces received from client
            inputLine = in.readLine();
            String message2 = rsa.decrypt(inputLine, prK);
            String[] parts = message2.split(" ");
            System.out.println("Received message from " + username + ": " + message2);
            nonceK = parts[1];
            
            // Send nonce again 
            out.println(rsa.encrypt(nonceK, puC));
            
            // Create symmetric key and sent to client
            String clientKey = serverkey + "_" + username;
            String encyptedMessage = rsa.invertedEncrypt(clientKey, prK);
            String encyptedMessage1 = encyptedMessage.substring(0, (encyptedMessage.length()/2));
            String encyptedMessage2 = encyptedMessage.substring(encyptedMessage.length()/2);
            String encryptedResponse1 = rsa.encrypt(encyptedMessage1, puC);
            String encryptedResponse2 = rsa.encrypt(encyptedMessage2, puC);
            out.println(encryptedResponse1 + " " + encryptedResponse2);
            
            
            out.println("You can start key distribution protocol!");
            
            while ((inputLine = in.readLine()) != null) {
                String[] commands = inputLine.split(" ");
                if (commands[0].equals("request")) {
                    if (commands.length != 2) {
                        out.println("To request: <request> <clientID>");
                    } else {
                        if (users.contains(commands[1]) && !requests.containsKey(commands[1])) {
                            String requestedKey = serverkey + "_" + commands[1] + "_" + username;
                            requests.put(commands[1], username);
                            out.println("encryptedKey " + aes.encrypt(requestedKey + " " + commands[1], clientKey));
                        } else {
                            out.println(commands[1] + "has not been connected to the server "
                                    + "or is making another request. Please wait!");
                        }
                    } 
                }
                else if (commands[0].equals("getkey")){
                    if (requests.containsKey(username)) {    
                        String combinedKey = serverkey + "_" + username + "_" + requests.get(username);
                        out.println("encryptedKey " + aes.encrypt(combinedKey + " " + requests.get(username), clientKey));
                        requests.remove(username);
                    } else {
                        out.println("Key is not found!");
                    }
                }
                
                else if (commands[0].equals("exit"))
                    break;
                
                else {
                    out.println("Invalid command!");
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(KDCServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
