package com.example.liny33.find_zies;

/**
 * Created by sine_XD on 12/11/15.
 */
import com.google.android.gms.plus.model.people.Person;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This is a simple server application. This server receive a string message
 * from the Android mobile phone and show it on the console.
 * Author by Lak J Comspace
 */
public class server {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
//    private static String message;
    private static ArrayList<PersonInfo> participants;
    private static ArrayList<PersonInfo> organizers;

    public static void main(String[] args) throws IOException{
        participants = new ArrayList<PersonInfo>();
        organizers = new ArrayList<PersonInfo>();
        try {
            serverSocket = new ServerSocket(1236); // Server socket
        } catch (IOException e) {
            System.out.println("Could not listen on port: 1236");
        }
        System.out.println("Server started. Listening to the port 1236");
        while (true) {

            clientSocket = serverSocket.accept(); // accept the client connection
            System.out.println("Accepting new thread.");

            MiniServer mini = new MiniServer(clientSocket);
            mini.start();
            System.out.println("New thread.");


        }
//        serverSocket.close();

    }

    private static void notifyEachOther() throws IOException {
//        for (PersonInfo organizer: organizers) {
            OutputStream os = organizers.get(0).getClientSocket().getOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(os);
            PrintWriter bw = new PrintWriter(os, true);
            bw.println("Hi we are cute organizers.");
            for (PersonInfo participant : participants) {
                bw.println(participant.getUsername());
                System.out.println("Message sent to the orgainizer is " + participant.getUsername());
            }
//            bw.flush();
//        }
        String address = organizers.get(0).getAddress();
        System.out.println("Participant size: " + participants.size());
        PrintWriter bw2 = null;
        for (PersonInfo participant : participants) {
            System.out.println(participant.getClientSocket());
//            OutputStream os2 = participant.getClientSocket().getOutputStream();

//            OutputStreamWriter osw2 = new OutputStreamWriter(participant.getOs());

            bw2 = new PrintWriter(participant.getOs(), true);
            bw2.println(address);
            System.out.println("Message sent to the participant is " + address);

        }

//        bw2.flush();
    }

    private static class MiniServer extends Thread {
        private Socket clientSocket = null;

        public MiniServer(Socket socket) {
            super("MiniServer");
            this.clientSocket = socket;
        }

        public void run(){
            try {
                inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader); // get the client message

                String username = bufferedReader.readLine();
                System.out.println("Username is " + username);

                boolean isOrganizer = Boolean.parseBoolean(bufferedReader.readLine());
                if (isOrganizer) {
                    String address = bufferedReader.readLine();
                    PersonInfo p = new PersonInfo(username, isOrganizer, address, clientSocket);
                    organizers.add(p);
                    System.out.println("If I am a organizer: " + isOrganizer);
                    System.out.println("Address is: " + address);
                } else {
//                        boolean isGoing = Boolean.parseBoolean(bufferedReader.readLine());
                    OutputStream os2 = clientSocket.getOutputStream();
                    System.out.println("If I am a organizer: " + isOrganizer);
                    PersonInfo p = new PersonInfo(username, isOrganizer, true, clientSocket, os2);
                    participants.add(p);
                }

                if (isOrganizer) {
                    while (true) {
                        String notify = bufferedReader.readLine();
                        System.out.println("Ready to notify");
                        if (notify.equalsIgnoreCase("notifyall")) {
                            System.out.println("Notifying");
                            notifyEachOther();
                            organizers.clear();
                            participants.clear();
                            break;
                        }
                    }
                }
                inputStreamReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Problem in message reading");
            }
        }
    }

}
