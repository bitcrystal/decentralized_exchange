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
        final boolean upnp = UPnPServerSocketFactory.hasUPnPSupport();
        try {
            String x = "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,,";
            for (int i = 0; i < 5; i++) {
                x += x;
            }
            new Thread(new Runnable() {

                public void run() {
                    try {
                        ServerSocket serverSocket = null;
                        if (upnp) {
                            serverSocket = UPnPServerSocketFactory.getServerSocket(5674);
                        } else {
                            serverSocket = new ServerSocket(5674);
                        }
                        Socket accept = serverSocket.accept();
                        TCPClientSecurity tCPClientSecurity = new TCPClientSecurity(accept);
                        String y = tCPClientSecurity.recvSecurity();
                        System.out.println(y);
                    } catch (IOException ex) {
                        Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
            Socket socket = new Socket("127.0.0.1", 5674);

            TCPClientSecurity tCPClientSecurity = new TCPClientSecurity(socket);
            tCPClientSecurity.sendSecurity(x);
            System.out.println(x);
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
