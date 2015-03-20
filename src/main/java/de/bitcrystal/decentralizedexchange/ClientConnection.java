/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class ClientConnection implements Runnable {
    private static Map<String,String> traders=new ConcurrentHashMap<String, String>();
    private static Map<String,String> traderspw=new ConcurrentHashMap<String, String>();
    private TCPClient server;
    private String command;

    public ClientConnection(TCPClient server,String command) {
        this.server = server;
        this.command=command;
    }

    public void run() {
        try {
            String recv = server.recv();
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            String decodeDataSecurityEmail = bitcrystalrpc.decodeDataSecurityEmail(recv);
            String decodeDataSecurityEmailHash = bitcrystalrpc.decodeDataSecurityEmailHash(decodeDataSecurityEmail);
            if (!decodeDataSecurityEmailHash.contains(",")) {
                this.server.close();
                return;
            }
            String[] split = decodeDataSecurityEmailHash.split(",");
            if (split.length < 1) {
                this.server.close();
                return;
            }
            switch (split.length) {
                case 5: {
                    if (split[0].equalsIgnoreCase("register")) {
                        if(traders.containsKey(split[3]))
                        {
                            this.server.close();
                            return;
                        }
                        String bitcoinAddressOfPubKey = bitcrystalrpc.getBitcoinAddressOfPubKey(split[2]);
                        if(!split[1].equals(bitcoinAddressOfPubKey))
                        {
                            this.server.close();
                            return;
                        }
                        traders.put(split[3], split[1]);
                        traderspw.put(split[3], split[4]);
                    }
                }
                break;
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
