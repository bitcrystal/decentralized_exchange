/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

/**
 *
 * @author ABC
 */
import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.io.*;
import java.net.*;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

final class TCPServer {

    private int port;
    private ServerSocket serverSocket;
    private boolean isrunning;
    private boolean canrunned;
    private boolean canstopped;

    public TCPServer(int port) {
        isrunning = false;
        canrunned = false;
        canstopped = false;
        serverSocket = null;
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (!isValidConnection()) {
                this.stop();
            } else {
                canrunned = true;
            }
        }
    }

    public void start() {
        if (canrunned) {
            isrunning = true;
            canrunned = false;
        } else {
            return;
        }
        new Thread(new Runnable() {

            public void run() {
                try {
                    while (isrunning) {
                        Socket connectionSocket = serverSocket.accept();
                        clientConnection(connectionSocket);
                    }
                    canstopped = true;
                } catch (IOException ex) {
                    Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    private boolean isValidConnection() {
        return serverSocket != null;
    }

    public void stop() {
        if (!isValidConnection()) {
            isrunning = false;
            return;
        }
        if (!isrunning) {
            return;
        }
        this.isrunning = false;
        while (!canstopped);
        this.canrunned = false;
        try {
            this.serverSocket.close();
            this.serverSocket = null;
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clientConnection(final Socket connectionSocket) {
        new Thread(new Runnable() {

            public void run() {
                TCPClient tcpClient = new TCPClient(connectionSocket);
                if (!tcpClient.isValidConnection()) {
                    return;
                }
                new Thread(new ServerConnection(tcpClient)).start();
            }
        }).start();
    }
}