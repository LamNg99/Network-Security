/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project2;

/**
 *
 * @author lamnguyen
 */

public class SiriProtocol {
    private static final int WAITING = 0;
    private static final int SENTQUESTION = 1;
    private static final int ANOTHER = 2;

    private String[] questions = { "What are you made of?", 
                                  "Windows or Mac?", 
                                  "What is the meaning of life?",
                                  "How much do you earn?"};
    private String[] answers = { "It’s complicated, but definitely not sugar, spice, or puppy dog tails.",
                                 "I’m pretty loyal to Apple. It’s just how I’m made.",
                                 "I Kant answer that. Ha ha!",
                                 "I'm not in it for the money. But I do accept verbal high fives."};
    
    // Function to find the index of an element
    public static int findIndex(String arr[], String t) {
 
        // if array is Null
        if (arr == null) {
            return -1;
        }
 
        // find length of array
        int len = arr.length;
        int i = 0;
 
        // traverse in the array
        while (i < len) {
 
            // if the i-th element is t
            // then return the index
            if (arr[i].equalsIgnoreCase(t)) {
                return i;
            }
            else {
                i = i + 1;
            }
        }
        return -1;
    }

    public String processInput(String theInput) {
        String theOutput = null;
 
        if (state == WAITING) {
            theOutput = "Welcome to Siri, you can start asking questions!";
            state = SENTQUESTION;
        } else if (state == SENTQUESTION) {
            if (findIndex(questions, theInput) >= 0) {
                theOutput = answers[findIndex(questions, theInput)] + " - Do you want to continue? (yes/no)";
            } else {
                theOutput = "Question is not found. Do you want to try another question? (yes/no)";
            }
            state = ANOTHER;
        } else if (state == ANOTHER) {
            if (theInput.equalsIgnoreCase("yes")) {
                theOutput = "What do you want to ask?";
                state = SENTQUESTION;
            } else {
                theOutput = "Bye!";
                state = WAITING;
            }
        }
        return theOutput;
    }
}
