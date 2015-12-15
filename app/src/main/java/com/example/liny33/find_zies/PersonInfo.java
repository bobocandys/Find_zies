package com.example.liny33.find_zies;

/**
 * Created by sine_XD on 12/13/15.
 */

import java.io.OutputStream;
import java.net.Socket;

/**
 * Easy class for storing a person's general data.
 */
public class PersonInfo {
    private String username;
    private boolean isOrganizer;
    private String address;
    private boolean isGoing;
    private Socket clientSocket;
    private OutputStream os;

    public PersonInfo (String username, boolean isOrganizer, String address) {
        this.username = username;
        this.isOrganizer = isOrganizer;
        this.address = address;
    }

    public PersonInfo (String username, boolean isOrganizer) {
        this.username = username;
        this.isOrganizer = isOrganizer;

    }

    public PersonInfo (String username, boolean isOrganizer, boolean isGoing, Socket clientSocket, OutputStream os) {
        this.username = username;
        this.isOrganizer = isOrganizer;
        this.isGoing = isGoing;
        this.clientSocket = clientSocket;
        this.os = os;
    }

    public PersonInfo (String username, boolean isOrganizer, String address, Socket clientSocket) {
        this.username = username;
        this.isOrganizer = isOrganizer;
        this.address = address;
        this.clientSocket = clientSocket;
    }

    public OutputStream getOs() {
        return os;
    }

    public String getUsername() {
        return username;
    }

    public boolean getIsOrganizer() {
        return isOrganizer;
    }

    public String getAddress() {
        return address;
    }

    public boolean getIsGoing() {
        return isGoing;
    }

    public Socket getClientSocket() {
        return  clientSocket;
    }
}
