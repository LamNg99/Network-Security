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
 
public class SiriMultiServer {
    public static void main(String[] args) throws IOException {
 
    if (args.length != 1) {
        System.err.println("Usage: java SiriMultiServer <port number>");
        System.exit(1);
    }
 
        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
         
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            while (listening) {
                new SiriMultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
