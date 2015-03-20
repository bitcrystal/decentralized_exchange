/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class BitcoinDecRegister {

    private static RPCApp bitcoinrpc = null;
    private static RPCApp bitcrystalrpc = null;
    private static boolean isInit = false;

    private static void init() {
        if (isInit) {
            return;
        }
        try {
            bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
        } catch (Exception ex) {
            Logger.getLogger(BitcoinDecRegister.class.getName()).log(Level.SEVERE, null, ex);
        }
        isInit = true;
    }
    
    public void register(List<TCPClient> tcpClients)
    {
        
    }
}
