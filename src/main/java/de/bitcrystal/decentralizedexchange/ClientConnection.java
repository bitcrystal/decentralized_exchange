/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import de.bitcrystal.decentralizedexchange.security.BitCrystalKeyGenerator;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.net.Socket;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class ClientConnection implements Runnable {

    private static Map<String, String> trader = new ConcurrentHashMap<String, String>();
    private static List<String> list = new CopyOnWriteArrayList<String>();
    private static PublicKey lastPubKey=null;
    private static List<String> tradeAccounts  = new CopyOnWriteArrayList<String>();
    private TCPClient server;
    private String command;
    public ClientConnection(TCPClient server, String command) {
        this.server = server;
        this.command = command;
    }

    public void run() {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (command==null||command.isEmpty()) {
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            if (!command.contains(" ")) {
                command = command + " ";
            }
            String[] split = command.split(" ");
            if (split.length < 1) {
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            switch (split.length) {
                case 1: {
                    if(split[0].equalsIgnoreCase("ADD"))
                    {
                        String newAddress = bitcoinrpc.getNewAddress();
                        String pubKey = bitcoinrpc.getPubKey(newAddress);
                        String privKey = bitcoinrpc.getPrivKey(newAddress);
                        bitcrystalrpc.importPrivKey(privKey);
                        this.server.send("add,"+pubKey);
                        this.server.recv();
                        this.server.close();
                    }
                }
                break;
                   
                case 2:
                {
                    if(split[0].equalsIgnoreCase("TRADEWITH"))
                    {
                        this.server.send("tradewith,"+split[1]);
                        String recv = this.server.recv();
                        if(recv.equals("E_ERROR")){
                            this.server.close();
                            return;
                        }
                        if(!recv.contains(","))
                        {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        
                        String[] split1 = recv.split(",");
                        if(split1.length!=4)
                        {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        
                        Object[] values1 = {split1[0],split[2]};
                        Object[] values2 = {split1[1],split[2]};
                        Object[] values3 = {split1[0],split[3]};
                        Object[] values4 = {split1[1],split[3]};
                        if(!tradeAccounts.contains(split[2]))
                        {
                             bitcoinrpc.addmultisigaddressex(values1);
                             bitcrystalrpc.addmultisigaddressex(values2);
                             tradeAccounts.add(split[2]);
                            
                        }
                        if(!tradeAccounts.contains(split[3]))
                        {
                            bitcoinrpc.addmultisigaddressex(values3);
                            bitcrystalrpc.addmultisigaddressex(values4);
                            tradeAccounts.add(split[3]);
                        }
                        this.server.send("ALL_OK");
                        this.server.close();
                    }
                }
                break;
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
