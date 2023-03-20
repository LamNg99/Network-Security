/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lab4;

/**
 *
 * @author lamnguyen
 */

import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.util.*;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KDCServerThread extends Thread {
    private Socket socket = null;
    private static  ArrayList<String> users = new ArrayList<>();
    private PrintWriter out;
 
    public KDCServerThread(Socket socket) {
        super("KDCServerThread");
        this.socket = socket;
    }
     
    @Override
    public void run() {
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); 
            String inputLine, outputLine;
            String serverkey = "super_secret_key";
            String IDENTITY = "KDCServer";
            String dir="/Users/lamnguyen/Desktop/School/COE817/Lab4";
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
            
            if(users.size() == 3){
                System.out.println("There are already 3 users in the chat server");
                return;
            }
            
            out.println("Please enter your username:");
            inputLine = in.readLine();
            username = inputLine;
            
            while(users.contains(username) || users.size() >= 3) {
                String message = users.size() >= 3 ?  "There are already three users registered" : "Username already exists, please choose another one.";
                out.println(message);
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
            String message2nonceC = rsa.decrypt(inputLine, prK);
            inputLine = in.readLine();
            String message2nonceK = rsa.decrypt(inputLine, prK);
            //String[] parts = message2.split(" ");
            System.out.println("Received message from " + username + ": " + message2nonceC + " " + message2nonceK);
            //nonceK = parts[1];
            
            // Send nonce again 
            out.println(rsa.encrypt(nonceK, puC));
            
            // Create symmetric key and sent to client
            String clientKey = serverkey + "_" + username;
            String encypted = rsa.encrypt(clientKey, puC);
            String chatKey = "";
            out.println(encypted);
            out.println("You can start key distribution protocol!");
            
            while ((inputLine = in.readLine()) != null) {
                String[] inputSplits = inputLine.split(" ");
                if (inputSplits[0].equals("chat")) {
                    if(users.size() <= 1){
                        out.println("usersNotEnough.");
                        continue;
                    }
                    chatKey = serverkey;
                    //this makes the AES key
                    for(int i = 0; i < users.size(); i++){
                        chatKey = chatKey + "_" + users.get(i);
                    }
                    out.println("chatkey " + aes.encrypt(chatKey, clientKey));
                    continue;
                }else if(inputSplits[0].equals("encChat")){
                    String senderID = inputSplits[1];
                    String encryptedChat = inputSplits[2];
                    String signature = inputSplits[3];
                    String chatNonce = inputSplits[4];
                    List<KDCServerThread> connections = KDCServer.connections;
                    String message = senderID + " " + encryptedChat + " " + signature + " " + chatNonce;
                    
                    for(int i = 0; i < connections.size(); i++){
                        if(!connections.get(i).socket.equals(this.socket)){
                            chatKey = serverkey;
                            //this makes the AES key - you do it again in case it has changed.
                            for(int j = 0; j < users.size(); j++){
                                chatKey = chatKey + "_" + users.get(j);
                            }

                            String currClientKey = "super_secret_key" + "_" + users.get(i);

                            connections.get(i).out.println("incoming " + message);
                            connections.get(i).out.println(aes.encrypt(chatKey, currClientKey));
                            connections.get(i).out.println("finished");
                           // System.out.println("here " + chat_nonce_Id);
                            //String chat_nonce_dec = aes.decrypt(chat_nonce_enc, chatKey);
                        }
                    }
                    out.println("finished");
                    //stuff the client should do to forwarded msg
                    // PublicKey senderKey = rsa.loadPublicKey(dir, senderID);
                    
                    // String decryptedChat = aes.decrypt(encryptedChat, chatKey);
                    // System.out.println(decryptedChat);
                    // String signatureHash = rsa.sha256(decryptedChat);
                    // String decryptedSignature = rsa.invertedDecrypt(signature, senderKey);
                    // if(signatureHash.equals(decryptedSignature)){
                    //     System.out.println("Signature match!");
                    // } 
                }else if (inputLine.equals("exit")){
                    for(int i = 0; i < users.size(); i++){
                        if(users.get(i).equals(inputSplits[1])){
                            users.remove(i);
                        }
                    }
                }else {
                    out.println("Invalid command!");
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(KDCServerThread.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}