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
import org.json.JSONException;
import org.json.JSONObject;

final class TCPClient {

    private String host;
    private int port;
    private Socket clientSocket;
    private InputStream in;
    private OutputStream out;
    private BufferedReader din;
    private DataOutputStream dout;

    public TCPClient(Socket socket) {
        this.clientSocket = null;
        this.clientSocket = null;
        try {
            this.clientSocket = socket;
            this.port = clientSocket.getPort();
            this.host = clientSocket.getInetAddress().getHostAddress();
            in = this.clientSocket.getInputStream();
            out = this.clientSocket.getOutputStream();
            din = new BufferedReader(new InputStreamReader(in));
            dout = new DataOutputStream(out);
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
        this.clientSocket = null;
        try {
            this.clientSocket = new Socket(host, port);
            this.port = port;
            this.host = host;
            in = this.clientSocket.getInputStream();
            out = this.clientSocket.getOutputStream();
            din = new BufferedReader(new InputStreamReader(in));
            dout = new DataOutputStream(out);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (!isValidConnection()) {
                this.close();
            }
        }
    }

    public boolean isValidConnection() {
        return this.din != null && this.dout != null && this.in != null && this.out != null && this.clientSocket != null;
    }

    public void send(String message) {
        if (!isValidConnection()) {
            return;
        }
        try {
            dout.writeBytes(message);
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
            return din.readLine();
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "E_READ_ERROR";
    }

    public void sendJSONObject(JSONObject object) {
        if (!isValidConnection()) {
            return;
        }
        this.send(object.toString(), 100);
        return;
    }

    public JSONObject recvJSONObject() {
        if (!isValidConnection()) {
            return null;
        }
        String recv = this.recv(100);
        if (recv == null) {
            return null;
        }
        try {
            return new JSONObject(recv);
        } catch (JSONException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void sendRaw(byte[] bytes, int off, int len) {
        if (!isValidConnection()) {
            return;
        }
        try {
            this.out.write(bytes, off, len);
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
    }

    public byte[] recvRaw(int off, int len) {
        if (!isValidConnection()) {
            return new byte[]{};
        }
        byte[] bytes = new byte[len];
        byte[] readBytes = new byte[]{};
        try {
            int read = this.in.read(bytes, off, len);
            if (read <= 0) {
                return readBytes;
            }
            readBytes = new byte[read];
            for (int i = 0; i < read; i++) {
                readBytes[i] = bytes[i];
            }
            return readBytes;
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            return new byte[]{};
        }
    }

    private boolean writeByte(OutputStream outputStream, byte b) {
        try {
            outputStream.write(b & 0xFF);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private void write(OutputStream outputStream, byte[] b, int off, int len) {
        int trys_ = 0;
        int i = 0;
        while (i < len && trys_ < 10) {
            boolean trysb = writeByte(outputStream, b[i + off]);
            if (!trysb) {
                trys_++;
                continue;
            }
            i++;
            trys_ = 0;
        }
        try {
            outputStream.flush();
        } catch (IOException ex) {
        }
    }

    public void send(String data, int buffer) {
        if (buffer <= 0) {
            buffer = 100;
        }
        if (buffer % 2 != 0) {
            buffer = 100;
        }
        try {

            OutputStream outputStream = new BitCrystalOutputStreamEx(this.getOutputStream());
            byte[] b = data.getBytes("UTF-8");
            int length = b.length;
            int maxi = length / buffer;
            int off = 0;
            for (int i = 0; i < maxi; i++) {
                outputStream.write(b, off, buffer);
                outputStream.flush();
                off += buffer;
            }
            maxi = length % buffer;
            if (maxi != 0) {
                outputStream.write(b, off, maxi);
                outputStream.flush();
            }
        } catch (Exception ex) {
            Logger.getLogger(TCPClientSecurity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private byte readByte(InputStream inputStream) {
        int read = 0;
        try {
            read = inputStream.read();
            if (read == (byte)-1) {
                return (byte) -1;
            }
            return (byte) read;
        } catch (IOException ex) {
            return (byte) -1;
        }
    }

    private int read(InputStream inputStream, byte[] b, int off, int len) {
        int i = 0;
        int trys_ = 0;
        byte read = 0;
        while (i < len && trys_ < 10) {
            read = readByte(inputStream);
            if (read == (byte) -1) {
                trys_++;
                continue;
            }
            trys_ = 0;
            b[i + off] = read;
            i++;
        }
        return i;
    }

    public String recv(int buffer) {
        if (buffer <= 0) {
            buffer = 50;
        }
        if (buffer / 2 <= 0 || buffer % 2 != 0) {
            buffer = 50;
        } else {
            buffer = buffer / 2;
        }
        try {
            InputStream in = new BitCrystalInputStreamEx(this.getInputStream());
            int len = buffer;
            byte[] b = new byte[buffer];
            int off = 0;
            int read = 0;
            String string = "";
            do {
                read = in.read(b, off, len);
                if (read == buffer) {
                    string += new String(b, "UTF-8");
                } else if (read > 0) {
                    byte[] bytes = new byte[read];
                    for (int i = 0; i < read; i++) {
                        bytes[i] = b[i];
                    }
                    string += new String(bytes, "UTF-8");
                    if(in.available()>0)
                        read=buffer;
                }
            } while (read == buffer);
            return string;
        } catch (IOException ex) {
            Logger.getLogger(TCPClientSecurity.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public OutputStream getOutputStream() {
        return out;
    }

    public InputStream getInputStream() {
        return in;
    }

    public void close() {
        if (this.din != null) {
            try {
                this.din.close();
                this.din = null;
            } catch (IOException ex) {
                Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (this.dout != null) {
            try {
                this.dout.close();
                this.dout = null;
            } catch (IOException ex) {
                Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
                this.clientSocket = null;
            } catch (IOException ex) {
                Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getHostAddress() {
        return host;
    }

    public int getHostPort() {
        return port;
    }

    public TCPClientSecurity getSecurityClient() {
        return new TCPClientSecurity(this);
    }
}