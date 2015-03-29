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
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

final class TCPClient {

    private String host;
    private int port;
    private Socket clientSocket;
    private DataOutputStream out;
    private BufferedReader in;

    public TCPClient(Socket socket) {
        this.in = null;
        this.out = null;
        this.clientSocket = null;
        try {
            this.port = socket.getPort();
            this.host = socket.getInetAddress().getHostAddress();
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (!isValidConnection()) {
                this.close();
            }
        }
    }

    public TCPClient(String host) {
        this(host, 6739);
    }

    public TCPClient(int port) {
        this("127.0.0.1", port);
    }

    public TCPClient(String host, int port) {
        this.in = null;
        this.out = null;
        this.clientSocket = null;
        this.host = host;
        this.port = port;
        try {
            this.clientSocket = new Socket(host, port);
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (!isValidConnection()) {
                this.close();
            }
        }
    }

    public boolean isValidConnection() {
        return this.in != null && this.out != null && this.clientSocket != null;
    }

    public void send(String message) {
        if (!isValidConnection()) {
            return;
        }
        try {
            out.writeBytes(message);
        } catch (UnknownHostException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String recv() {
        if (!isValidConnection()) {
            return "NO_SOCKET";
        }
        try {
            return in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "E_READ_ERROR";
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public DataOutputStream getOutputStream() {
        return out;
    }

    public BufferedReader getInputStream() {
        return in;
    }

    public void close() {
        if (this.in != null) {
            try {
                this.in.close();
                this.in = null;
            } catch (IOException ex) {
                Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (this.out != null) {
            try {
                this.out.close();
                this.out = null;
            } catch (IOException ex) {
                Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (this.clientSocket != null) {
            try {
                this.clientSocket.close();
                this.out = null;
            } catch (IOException ex) {
                Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String getHostAddress() {
        return getSocket().getInetAddress().getHostAddress();
    }
    
    public TCPClientSecurity getSecurityClient()
    {
        return new TCPClientSecurity(this);
    }
}