/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.google.gson.JsonObject;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;
import de.bitcrystal.decentralizedexchange.security.BitCrystalKeyGenerator;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author ABC
 */
public class ServerConnection implements Runnable {

    private TCPClient client;
    private static List<String> pubKeys = new CopyOnWriteArrayList<String>();
    private static Map<String, String> pubKeysMap = new ConcurrentHashMap<String, String>();
    private static Map<String, String> pubKeysMap2 = new ConcurrentHashMap<String, String>();
    private static List<String> serverPubKeys = new CopyOnWriteArrayList<String>();
    private static List<String> tradeAccounts = new CopyOnWriteArrayList<String>();

    public ServerConnection(TCPClient client) {
        this.client = client;
    }

    public void run() {
        String recv = client.recv();
        if (recv == null || recv.isEmpty()) {
            this.client.close();
            return;
        }
        if (recv.startsWith("add,")) {
            String hostAddress = this.client.getSocket().getInetAddress().getHostAddress();
            try {
                RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
                RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
                String[] split = recv.split(",");
                if (!pubKeys.contains(split[1])) {
                    pubKeys.add(split[1]);
                    pubKeysMap.put(hostAddress, split[1]);
                    pubKeysMap2.put(split[1], hostAddress);
                    String newAddress = bitcoinrpc.getNewAddress();
                    String pubKey = bitcoinrpc.getPubKey(newAddress);
                    String privKey = bitcoinrpc.getPrivKey(newAddress);
                    bitcrystalrpc.importPrivKey(privKey);
                    serverPubKeys.add(pubKey);
                    this.client.send("ALL_OK");
                    Thread.sleep(3000L);
                    this.client.close();
                }
                this.client.send("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();

            } catch (Exception ex) {
                try {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                } catch (InterruptedException ex1) {
                    this.client.send("E_ERROR");
                    this.client.close();
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }

        if (recv.startsWith("tradewith,")) {
            String hostAddress = this.client.getSocket().getInetAddress().getHostAddress();
            try {
                RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
                RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
                String[] split = recv.split(",");
                if (!pubKeys.contains(split[1])) {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                }
                if (!pubKeysMap.containsKey(hostAddress)) {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                }
                if (!pubKeysMap2.containsKey(split[1])) {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                }
                String get = pubKeysMap2.get(split[1]);
                if (get.equals(hostAddress)) {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                }
                if (pubKeys.size() <= 0) {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                }
                String get1 = pubKeys.get(pubKeys.size() - 1);
                pubKeys.remove(pubKeys.size() - 1);
                String get2 = pubKeysMap.get(hostAddress);
                String ba = bitcoinrpc.getBitcoinAddressOfPubKey(get2);
                String ba2 = bitcoinrpc.getBitcoinAddressOfPubKey(split[1]);
                String account = ba + "," + ba2;
                String account2 = ba2 + "," + ba;
                Object[] values = {get2, split[1], get1};
                String createmultisigaddressex = bitcoinrpc.createmultisigaddressex(values);
                String createmultisigaddressex2 = bitcrystalrpc.createmultisigaddressex(values);
                this.client.send(createmultisigaddressex + "," + createmultisigaddressex2 + "," + account + "," + account2);
                this.client.recv();
                this.client.close();
                Object[] values2 = {createmultisigaddressex, account};
                Object[] values3 = {createmultisigaddressex2, account};
                Object[] values4 = {createmultisigaddressex, account2};
                Object[] values5 = {createmultisigaddressex2, account2};
                if (!tradeAccounts.contains(account)) {
                    bitcoinrpc.addmultisigaddressex(values2);
                    bitcrystalrpc.addmultisigaddressex(values3);
                    tradeAccounts.add(account);
                }
                if (!tradeAccounts.contains(account2)) {
                    bitcoinrpc.addmultisigaddressex(values4);
                    bitcrystalrpc.addmultisigaddressex(values5);
                    tradeAccounts.add(account2);
                }
            } catch (Exception ex) {
                try {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                } catch (InterruptedException ex1) {
                    this.client.send("E_ERROR");
                    this.client.close();
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }
}
