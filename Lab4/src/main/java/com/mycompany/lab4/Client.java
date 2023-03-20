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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Thread;

public class Client {
    static String chatKey = "";
    static AtomicInteger chatNonce = new AtomicInteger(10925);
    public static void main(String[] args) throws IOException, Exception {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java Client <host name> <port number>");
            System.exit(1);
        }
 
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        BufferedReader stdIn;
        PrintWriter out;
        List<String> messages = Collections.synchronizedList(new ArrayList<String>());
        String currId;
        try{
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            Socket kkSocket = new Socket(hostName, portNumber);
            BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            final Socket socket = kkSocket;

            
            String fromServer;
            String fromUser;
            String IDENTITY = "";
            String dir="/Users/lamnguyen/Desktop/School/COE817/Lab4";
            
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

            currId = IDENTITY;
            
            // Receive Alice's identity and nonce
            fromServer = in.readLine();
            String message1 = rsa.decrypt(fromServer, prC);
            String[] parts = message1.split(" ");
            System.out.println("Received message: " + message1 );
            String identityK = parts[0];
            String nonceK = parts[1].trim();
            
            // Load server public key
            PublicKey puK = rsa.loadPublicKey(dir, identityK);
            
            // Encrypt nonces and send to sever
            String message2nonceC = nonceC;
            String message2nonceK = nonceK;
            String message2nonceCEnc = rsa.encrypt(message2nonceC, puK);
            String message2nonceKEnc = rsa.encrypt(message2nonceK, puK);
            out.println(message2nonceCEnc);
            out.println(message2nonceKEnc);
            
            // Recieve sever nonce
            fromServer = in.readLine();
            System.out.println("Received message: " + rsa.decrypt(fromServer, prC));
            String encryptedKey = in.readLine();
            String clientKey = rsa.decrypt(encryptedKey, prC);
            
            System.out.println("Received message Decrypted key: " + clientKey);
            
            System.out.println("****************** Key Distribution Session ******************");
            //old one from lab 3
            //System.out.println("To request: <request> <clientID>");
            System.out.println("To chat: <chat>");
            System.out.println("To exit: <exit>");
            System.out.println("**************************************************************");

            Thread processStdin = new Thread(){
                @Override
                public void run(){
                    try{
                        String userInput;
                        PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        System.out.println("Inside the thread");
                        
                        while((userInput = stdIn.readLine()) != null){
                            if(userInput.equals("exit")){
                                serverOut.println("exit " + currId );
                                return;
                            }else if(userInput.equals("chat")){
                                //do chat shit
                                serverOut.println("chat");
                                   System.out.println("Enter a chat message.");
                                   String chatMsg = stdIn.readLine();
                                   String messageHash = rsa.sha256(chatMsg);
                                   String signatureClient = rsa.invertedEncrypt(messageHash, prC);
                                   String clientNonce = Integer.toString(Client.chatNonce.get());
                                   serverOut.println("encChat " + aes.encrypt(currId, chatKey) + " " +  aes.encrypt(chatMsg, chatKey) + " " + signatureClient + " " + aes.encrypt(clientNonce, chatKey));
                                   System.out.println("Message sent to all users!");
                            }else{
                                System.out.println("invalid input");
                            }
                        }
                    }catch(Exception e){
                        System.out.println(e);
                    }
  
                }
            };

            Thread processServerMessages = new Thread(){
                @Override
                public void run(){
                    try{
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter toServer = new PrintWriter(kkSocket.getOutputStream(), true);
                        String serverMsg;
                        while(true){
                            serverMsg = in.readLine();
                            if(serverMsg != null){
                                String[] serverMsgSplit = serverMsg.split(" ");
                                if(serverMsgSplit[0].equals("exit")){
                                    break;
                                }else if(serverMsgSplit[0].equals("chatkey")){
                                    chatKey = serverMsgSplit[1];
                                    chatKey = aes.decrypt(chatKey, clientKey);
                                }else if(serverMsgSplit[0].equals("incoming")){
                                    //re-read the chatkey
                                    chatKey = aes.decrypt(in.readLine(), clientKey);
                                    String senderId = aes.decrypt(serverMsgSplit[1], chatKey);
                                    String chatMessage = aes.decrypt(serverMsgSplit[2], chatKey);
                                    String signatureSender = serverMsgSplit[3];
                                    String chatNonceStr = aes.decrypt(serverMsgSplit[4], chatKey);

                                    PublicKey senderPublicKey = rsa.loadPublicKey(dir, senderId);

                                    String messageHash = rsa.sha256(chatMessage);
                                    String signatureSenderDec = rsa.invertedDecrypt(signatureSender, senderPublicKey);
                                    if(messageHash.equals(signatureSenderDec) && Integer.parseInt(chatNonceStr) == Client.chatNonce.get()){
                                        System.out.println("Signature Verified!");
                                        System.out.println("From " + senderId  + ": ---> " + chatMessage);
                                    }
                                }else if(serverMsgSplit[0].equals("finished")){
                                    Client.chatNonce.incrementAndGet();
                                }
                            }
                        }
                    }catch(Exception e){
                        System.out.println("Server thread error " + e);
                    }
                }
            };
            processServerMessages.start();
            processStdin.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }catch (Exception e){
            System.out.println(e);
        } 
    }
}