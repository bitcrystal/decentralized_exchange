/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import de.bitcrystal.decentralizedexchange.security.BitCrystalJSON;
import de.bitcrystal.decentralizedexchange.security.BitCrystalKeyGenerator;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONObject;

/**
 *
 * @author ABC
 */
public class Test {
    public static void main(String[] args) {
        try {
            JSONObject json = new JSONObject();
            json.put("test", "alter");
            new Thread(new Runnable() {

                public void run() {
                    try {
                        ServerSocket serverSocket = new ServerSocket(5674);
                        Socket accept = serverSocket.accept();
                        TCPClientSecurity tCPClientSecurity = new TCPClientSecurity(accept);
                        String string = tCPClientSecurity.recv();
                        System.out.println("cool");
                        System.out.println(string);
                    } catch (IOException ex) {
                        Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
           Socket socket = new Socket("127.0.0.1", 5674);
            TCPClientSecurity tCPClientSecurity = new TCPClientSecurity(socket);
            tCPClientSecurity.send(json.toString());
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
