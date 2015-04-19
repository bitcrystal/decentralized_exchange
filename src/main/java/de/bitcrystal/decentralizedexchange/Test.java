/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import de.bitcrystal.decentralizedexchange.security.BitCrystalJSON;
import de.bitcrystal.decentralizedexchange.security.BitCrystalKeyGenerator;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import de.bitcrystal.decentralizedexchange.upnp.UPnPServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONObject;

/**
 *
 * @author ABC
 */
public class Test {

    private static Map<String, String> cm = new ConcurrentHashMap<String, String>();

    public static void main(String[] args) {
        try {
            new Thread(new Runnable() {

                public void run() {
                    try {
                        ServerSocket serverSocket = null;
                        serverSocket = new ServerSocket(5674);
                        Socket accept = serverSocket.accept();
                        TCPClientSecurity tCPClientSecurity = new TCPClientSecurity(accept);
                        String recvSecurity = tCPClientSecurity.getTCPClient().recv(5);
                        System.out.println(recvSecurity);
                    } catch (IOException ex) {
                        Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
           // Thread.currentThread().sleep(10000);
            Socket socket = new Socket("127.0.0.1", 5674);

            TCPClientSecurity tCPClientSecurity = new TCPClientSecurity(socket);
            tCPClientSecurity.getTCPClient().send("hallo", 50);
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
