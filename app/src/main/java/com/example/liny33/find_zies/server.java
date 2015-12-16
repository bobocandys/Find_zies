package com.example.liny33.find_zies;

/**
 * Created by sine_XD on 12/11/15.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class server {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
//    private static String message;
    private static ArrayList<PersonInfo> participants;
    private static ArrayList<PersonInfo> organizers;
    private static boolean notifyAll = false;


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
            bw.println(participants.size() + " participant coming." );
            bw.println("These are the participants coming to your event: ");
            for (PersonInfo participant : participants) {
                bw.println(participant.getUsername());
                System.out.println("Message sent to the orgainizer is " + participant.getUsername());
            }
            bw.close();
//        }
//        String address = organizers.get(0).getAddress();
////        PrintWriter bw2 = null;
////        for (PersonInfo participant : participants) {
//            System.out.println(participants.get(0).getClientSocket());
////            OutputStream os2 = participant.getClientSocket().getOutputStream();
//
////            OutputStreamWriter osw2 = new OutputStreamWriter(participant.getOs());
//
//            PrintWriter bw2 = new PrintWriter(participants.get(0).getOs(), true);
//            bw2.println(address);
////            bw2.flush();
//            System.out.println("Message sent to the participant is " + address);
//
////        }
//        bw2.flush();
//        bw2.close();
//        closeAllClientSockets();
//        bw2.flush();
    }

//    private static void closeAllClientSockets() throws IOException {
//        organizers.get(0).getClientSocket().close();
//        participants.get(0).getClientSocket().close();
//    }

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
                            notifyAll = true;
                            notifyEachOther();
                            organizers.clear();
                            participants.clear();

                            break;
                        }
                    }
                } else {

                    while (!notifyAll) {

                    }
                    String address = organizers.get(0).getAddress();
                    System.out.println(participants.get(0).getClientSocket());

                    PrintWriter bw2 = new PrintWriter(participants.get(0).getOs(), true);
                    bw2.println(address);
                    System.out.println("Message sent to the participant is " + address);

                    bw2.flush();
                    bw2.close();
                    String test = bufferedReader.readLine();
//                    System.out.println("The participant received data from organizer: " + test);
                }
                inputStreamReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Problem in message reading");
            }
        }
    }

}
