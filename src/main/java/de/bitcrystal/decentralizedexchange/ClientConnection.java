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
    private static String tradeAccount  = "";
    private static String tradeAccount2  = "";
    private static String tradebtcry2btc="";
    private static String tradebtc2btcry="";
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
                    
                    if(split[0].equalsIgnoreCase("TRADEABORT"))
                    {
                        tradeAccount="";
                        tradebtc2btcry="";
                        tradebtcry2btc="";
                        this.server.send("E_ERROR");
                        this.server.close();
                        return;
                    }
                }
                break;
                   
                case 2:
                {
                    if(split[0].equalsIgnoreCase("TRADEWITH"))
                    {
                        if(!tradeAccount.isEmpty())
                        {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
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
                        if(!tradeAccount.equals(split[2]))
                        {
                             bitcoinrpc.addmultisigaddressex(values1);
                             bitcrystalrpc.addmultisigaddressex(values2);
                             tradeAccount=split[2];
                            
                        }
                        if(!tradeAccount2.equals(split[3]))
                        {
                            bitcoinrpc.addmultisigaddressex(values3);
                            bitcrystalrpc.addmultisigaddressex(values4);
                            tradeAccount2=split[3];
                        }
                        this.server.send("ALL_OK");
                        this.server.close();
                    }
                }
                break;
                    
                case 3:
                {
                    if(split[0].equalsIgnoreCase("CREATETRADEBTCRY2BTC"))
                    {
                        tradebtc2btcry="";
                        if(tradeAccount.isEmpty()||tradeAccount2.isEmpty())
                        {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        if(!tradebtcry2btc.isEmpty())
                        {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        this.server.send("E_ERROR");
                        this.server.close();
                        double amount=0;
                        double price=0;
                        try
                        {
                            amount=Double.parseDouble(split[1]);
                            price= Double.parseDouble(split[2]);
                        } catch (Exception ex) {
                            tradebtcry2btc="";
                            tradebtc2btcry="";
                            return;
                        }
                        tradebtcry2btc=amount+","+price+","+tradeAccount+","+tradeAccount2;
                        tradebtc2btcry="";
                    }
                    
                    if(split[0].equalsIgnoreCase("CREATETRADEBTC2BTCRY"))
                    {
                        tradebtcry2btc="";
                        if(tradeAccount.isEmpty()||tradeAccount2.isEmpty())
                        {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        if(!tradebtc2btcry.isEmpty())
                        {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        this.server.send("E_ERROR");
                        this.server.close();
                        double amount=0;
                        double price=0;
                        try
                        {
                            amount=Double.parseDouble(split[1]);
                            price= Double.parseDouble(split[2]);
                        } catch (Exception ex) {
                            tradebtc2btcry="";
                            tradebtcry2btc="";
                            return;
                        }
                        tradebtc2btcry=tradeAccount+","+amount+","+price+","+tradeAccount+","+tradeAccount2;
                        tradebtcry2btc="";
                    }
                }
                break;
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
