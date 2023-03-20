/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lab4;

/**
 *
 * @author lamnguyen
 */

import java.net.*;
import java.io.*;
import java.util.*;
 
public class KDCServer {
    static List<KDCServerThread> connections = new ArrayList<KDCServerThread>();
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java KDCServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            while (listening) {
                KDCServerThread thread = new KDCServerThread(serverSocket.accept());
                connections.add(thread);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
