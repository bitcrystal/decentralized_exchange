/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.google.gson.JsonObject;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
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
public class ServerConnection implements Runnable {

    private TCPClientSecurity client;
    private static List<String> pubKeys = new CopyOnWriteArrayList<String>();
    private static Map<String, String> addressesPubkeys = new ConcurrentHashMap<String, String>();
    private static Map<String, String> pubkeysAddresses = new ConcurrentHashMap<String, String>();
    private static Map<String, String> pubKeysMap = new ConcurrentHashMap<String, String>();
    private static Map<String, String> pubKeysMap2 = new ConcurrentHashMap<String, String>();
    private static Map<String, String> serverAddressesPubkeys = new ConcurrentHashMap<String, String>();
    private static Map<String, String> serverPubkeysAddresses = new ConcurrentHashMap<String, String>();
    private static List<String> serverPubKeys = new CopyOnWriteArrayList<String>();
    private static List<String> tradeAccounts = new CopyOnWriteArrayList<String>();
    private static Map<String, String> ipsTradeAccounts = new ConcurrentHashMap<String, String>();
    private static Map<String, String> ipsTradeAccounts2 = new ConcurrentHashMap<String, String>();
    private static Map<String, String> tradeAccountsIp = new ConcurrentHashMap<String, String>();
    private static Map<String, String> tradeAccountsIp2 = new ConcurrentHashMap<String, String>();
    private static Map<String, String> ips = new ConcurrentHashMap<String, String>();
    private static Map<String, String> addresses = new ConcurrentHashMap<String, String>();
    private static Map<String, String> syncedtrades = new ConcurrentHashMap<String, String>();
    private static Map<String, String> startedtrades = new ConcurrentHashMap<String, String>();
    private static Map<String, String> endtradesme = new ConcurrentHashMap<String, String>();
    private static Map<String, String> endtradesother = new ConcurrentHashMap<String, String>();
    private static Map<String, String> startedtradesaccount = new ConcurrentHashMap<String, String>();

    public ServerConnection(TCPClient client) {
        this.client = DecentralizedExchange.getSecurityClient(client);
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
                boolean isPubKey = bitcoinrpc.isValidPubKey(split[1]);
                if (!isPubKey) {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                }
                if (!pubKeys.contains(split[1])) {
                    String bitcoinAddressOfPubKey = bitcoinrpc.getBitcoinAddressOfPubKey(split[1]);
                    addressesPubkeys.put(bitcoinAddressOfPubKey, split[1]);
                    pubkeysAddresses.put(split[1], bitcoinAddressOfPubKey);
                    pubKeys.add(split[1]);
                    pubKeysMap.put(hostAddress, split[1]);
                    pubKeysMap2.put(split[1], hostAddress);
                    String newAddress = bitcoinrpc.getNewAddress();
                    String pubKey = bitcoinrpc.getPubKey(newAddress);
                    String privKey = bitcoinrpc.getPrivKey(newAddress);
                    bitcrystalrpc.importPrivKey(privKey);
                    serverPubKeys.add(pubKey);
                    serverAddressesPubkeys.put(newAddress, pubKey);
                    serverPubkeysAddresses.put(pubKey, newAddress);
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
                if (ips.containsKey(hostAddress)) {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                }
                RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
                RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
                String[] split = recv.split(",");
                if (addressesPubkeys.containsKey(split[1])) {
                    split[1] = addressesPubkeys.get(split[1]);
                }
                if (!pubKeys.contains(split[1]) && !split[1].contains(".")) {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } else {
                    if (!pubKeysMap.containsKey(split[1])) {
                        this.client.send("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    }
                    String get10 = pubKeysMap.get(split[1]);
                    if (!pubKeysMap2.containsKey(get10)) {
                        this.client.send("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    }
                    String get11 = pubKeysMap2.get(get10);
                    if (get11.equals(hostAddress)) {
                        this.client.send("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    }
                    split[1] = get10;
                }
                if (addresses.containsKey(hostAddress)) {
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
                String get1 = serverPubKeys.get(pubKeys.size() - 1);
                serverPubKeys.remove(pubKeys.size() - 1);
                String get2 = pubKeysMap.get(hostAddress);
                String ba = bitcoinrpc.getBitcoinAddressOfPubKey(get2);
                addresses.put(hostAddress, ba);
                String ba2 = bitcoinrpc.getBitcoinAddressOfPubKey(split[1]);
                String account = ba + "," + ba2;
                String account2 = ba2 + "," + ba;
                Object[] values = {get2, split[1], get1};
                String createmultisigaddressex = bitcoinrpc.createmultisigaddressex(values);
                String createmultisigaddressex2 = bitcrystalrpc.createmultisigaddressex(values);
                this.client.send(createmultisigaddressex + ",," + createmultisigaddressex2 + ",," + ba2);
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
                    ipsTradeAccounts.put(account, hostAddress);
                    ipsTradeAccounts2.put(account, get);
                    tradeAccountsIp.put(hostAddress, account);
                    tradeAccountsIp.put(get, account);
                }
                if (!tradeAccounts.contains(account2)) {
                    bitcoinrpc.addmultisigaddressex(values4);
                    bitcrystalrpc.addmultisigaddressex(values5);
                    tradeAccounts.add(account2);
                    ipsTradeAccounts.put(account2, hostAddress);
                    ipsTradeAccounts2.put(account2, get);
                    tradeAccountsIp2.put(hostAddress, account2);
                    tradeAccountsIp2.put(get, account2);
                }
                ips.put(hostAddress, get);
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


        if (recv.startsWith("synctrade;")) {
            String hostAddress = this.client.getSocket().getInetAddress().getHostAddress();
            if (syncedtrades.containsKey(hostAddress)) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                }
            }
            if (!tradeAccountsIp.containsKey(hostAddress) && !tradeAccountsIp2.containsKey(hostAddress)) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                }
            }
            String[] split = recv.split(";");
            if (split.length != 2) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                }
                return;
            }
            if (!ips.containsKey(hostAddress)) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                }
            }
            String get = ips.get(hostAddress);
            if (!ips.containsKey(get)) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                }
            }
            String get1 = ips.get(get);
            if (!get1.equals(hostAddress)) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                }
            }
            if (!addresses.containsKey(hostAddress) || !addresses.containsKey(get)) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                }
            }
            this.syncedtrades.put(hostAddress, split[1]);
            this.client.send("TRADE IS SYNCED");
            this.client.recv();
            this.client.close();
        }

        if (recv.startsWith("starttrade")) {
            String hostAddress = this.client.getSocket().getInetAddress().getHostAddress();
            if (!syncedtrades.containsKey(hostAddress)) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }

            String get = ips.get(hostAddress);
            if (!syncedtrades.containsKey(get)) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            String get1 = syncedtrades.get(hostAddress);
            String get2 = syncedtrades.get(get);
            if (!get1.contains(",,") || !get2.contains(",,")) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            if (!get1.startsWith("btc2btry") && !get1.startsWith("btcry2btc")) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            if (!get2.startsWith("btc2btry") && !get2.startsWith("btcry2btc")) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            String[] split = get1.split(",,");
            String[] split2 = get2.split(",,");
            if (split.length != split2.length) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            if (split.length != 5) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }

            if (!tradeAccounts.contains(split[3]) || !tradeAccounts.contains(split[4]) || !tradeAccounts.contains(split2[3]) || !tradeAccounts.contains(split2[4])) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }

            if (split[0].equals(split2[0])) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            if (!(split[1].equals(split2[2]) && split2[1].equals(split[2]))) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }

            if (!(split[3].equals(split2[4]) && split2[3].equals(split[4]))) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }

            String tradeAccount = split[3];
            String tradeWithAccount = split[4];
            String tradeAccount2 = split2[3];
            String tradeWithAccount2 = split2[4];
            if (!tradeAccount.contains(",") || !tradeWithAccount.contains(",") || !tradeAccount2.contains(",") || !tradeWithAccount2.contains(",")) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            if (startedtrades.containsKey(tradeAccount) || startedtrades.containsKey(tradeAccount2) || startedtrades.containsKey(tradeWithAccount) || startedtrades.containsKey(tradeWithAccount2)) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }

            String get3 = ipsTradeAccounts.get(tradeAccount);
            String get4 = ipsTradeAccounts2.get(tradeAccount2);
            if ((!get3.equals(hostAddress) && !get4.equals(hostAddress)) || (get3.equals(hostAddress) && get4.equals(hostAddress))) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            String[] tradeAccountAddresses = split[3].split(",");
            String[] tradeWithAccountAddresses = split[4].split(",");
            String[] tradeAccount2Addresses = split2[3].split(",");
            String[] tradeWithAccount2Addresses = split2[4].split(",");
            if (tradeAccountAddresses.length != 2 || tradeWithAccountAddresses.length != 2 || tradeAccount2Addresses.length != 2 || tradeWithAccount2Addresses.length != 2) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            double split_amount = 0;
            double split_price = 0;
            double split2_amount = 0;
            double split2_price = 0;
            try {
                split_amount = Double.parseDouble(split[1]);
                split_price = Double.parseDouble(split[2]);
                split2_amount = Double.parseDouble(split2[1]);
                split2_price = Double.parseDouble(split2[2]);
                if (split_amount <= 0 || split_price <= 0 || split2_amount <= 0 || split2_price <= 0) {
                    throw new Exception();
                }
                if (split_amount != split2_price || split2_amount != split_price) {
                    throw new Exception();
                }
            } catch (Exception ex2) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }


            if (split[0].equals("btc2btcry") && split2[0].equals("btcry2btc")) {
                try {
                    RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
                    RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
                    Object[] values = {tradeAccount, tradeAccountAddresses[1], split_price};
                    Object[] values2 = {tradeWithAccount, tradeAccountAddresses[0], split_amount};
                    if (bitcoinrpc.getBalance(tradeAccount) < split_price) {
                        try {
                            this.client.send("E_ERROR");
                            Thread.sleep(3000L);
                            this.client.close();
                            return;
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                            this.client.send("E_ERROR");
                            this.client.close();
                            return;
                        }
                    }
                    if (bitcrystalrpc.getBalance(tradeWithAccount) < split_amount) {
                        try {
                            this.client.send("E_ERROR");
                            Thread.sleep(3000L);
                            this.client.close();
                            return;
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                            this.client.send("E_ERROR");
                            this.client.close();
                            return;
                        }
                    }
                    String createrawtransaction_multisig = bitcoinrpc.createrawtransaction_multisig(values);
                    String createrawtransaction_multisig1 = bitcrystalrpc.createrawtransaction_multisig(values2);
                    String tradeAccountSend = "btc2btcry;;" + values[0] + ";;" + values[1] + ";;" + values[2] + ";;" + createrawtransaction_multisig;
                    String tradeWithAccountSend = "btcry2btc;;" + values2[0] + ";;" + values2[1] + ";;" + values2[2] + ";;" + createrawtransaction_multisig1;
                    startedtrades.put(tradeAccount, tradeAccountSend);
                    startedtrades.put(tradeWithAccount, tradeWithAccountSend);
                    client.send("ALL_OK");
                    client.recv();
                    client.close();
                } catch (Exception ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }

            if (split[0].equals("btcry2btc") && split2[0].equals("btc2btcry")) {
                try {
                    RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
                    RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
                    Object[] values = {tradeAccount, tradeAccountAddresses[1], split_price};
                    Object[] values2 = {tradeWithAccount, tradeAccountAddresses[0], split_amount};
                    if (bitcrystalrpc.getBalance(tradeAccount) < split_price) {
                        try {
                            this.client.send("E_ERROR");
                            Thread.sleep(3000L);
                            this.client.close();
                            return;
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                            this.client.send("E_ERROR");
                            this.client.close();
                            return;
                        }
                    }
                    if (bitcoinrpc.getBalance(tradeWithAccount) < split_amount) {
                        try {
                            this.client.send("E_ERROR");
                            Thread.sleep(3000L);
                            this.client.close();
                            return;
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                            this.client.send("E_ERROR");
                            this.client.close();
                            return;
                        }
                    }
                    String createrawtransaction_multisig = bitcrystalrpc.createrawtransaction_multisig(values);
                    String createrawtransaction_multisig1 = bitcoinrpc.createrawtransaction_multisig(values2);
                    String tradeAccountSend = "btcry2btc;;" + values[0] + ";;" + values[1] + ";;" + values[2] + ";;" + createrawtransaction_multisig;
                    String tradeWithAccountSend = "btc2btcry;;" + values2[0] + ";;" + values2[1] + ";;" + values2[2] + ";;" + createrawtransaction_multisig1;
                    startedtrades.put(tradeAccount, tradeAccountSend);
                    startedtrades.put(tradeWithAccount, tradeWithAccountSend);
                    startedtradesaccount.put(tradeAccount, tradeWithAccount);
                    startedtradesaccount.put(tradeWithAccount, tradeAccount);
                    client.send("ALL_OK");
                    client.recv();
                    client.close();
                } catch (Exception ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
        }

        if (recv.startsWith("endtrademe;")) {
            String[] split = recv.split(";");
            if (split.length != 2) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            if (!startedtrades.containsKey(split[1])) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            if (endtradesme.containsKey(split[1])) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }

            this.client.send(startedtrades.get(split[1]));
            String recv1 = this.client.recv();
            if (recv1.equals("E_ERROR")) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            endtradesme.put(split[1], recv1);
            this.client.send("ALL_OK");
            this.client.close();
            return;
        }

        if (recv.startsWith("endtradeother;")) {
            String[] split = recv.split(";");
            if (split.length != 2) {
                this.client.send("E_ERROR");
                this.client.close();
                return;
            }
            if (!startedtradesaccount.containsKey(split[1])) {
                this.client.send("E_ERROR");
                this.client.close();
                return;
            }
            String get = startedtradesaccount.get(split[1]);
            if (!endtradesme.containsKey(get) || !endtradesme.containsKey(split[1])) {
                this.client.send("E_ERROR");
                this.client.close();
                return;
            }
            String get1 = endtradesme.get(get);
            this.client.send(get1);
            String recv2 = this.client.recv();
            if (recv2.equals("E_ERROR")) {
                try {
                    this.client.send("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.send("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            endtradesother.put(get, recv2);
            this.client.send("ALL_OK");
            this.client.close();
        }

        if (recv.startsWith("endtrade")) {
            try {
                String hostAddress = this.client.getHostAddress();
                if (!ips.containsKey(hostAddress)) {
                }
                String otherip = ips.get(hostAddress);
                String tradeAccount = "";
                String tradeWithAccount = "";
                if (tradeAccountsIp.containsKey(hostAddress)) {
                    tradeAccount = tradeAccountsIp.get(hostAddress);
                } else if (tradeAccountsIp2.containsKey(hostAddress)) {
                    tradeAccount = tradeAccountsIp.get(hostAddress);
                }

                if (tradeAccountsIp.containsKey(otherip)) {
                    tradeWithAccount = tradeAccountsIp.get(otherip);
                } else if (tradeAccountsIp2.containsKey(otherip)) {
                    tradeWithAccount = tradeAccountsIp.get(otherip);
                }

                if (tradeAccount.isEmpty() || tradeWithAccount.isEmpty()) {
                    try {
                        this.client.send("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                        this.client.send("E_ERROR");
                        this.client.close();
                        return;
                    }
                }
                if (!endtradesother.containsKey(tradeAccount) || !endtradesother.containsKey(tradeWithAccount)) {
                    try {
                        this.client.send("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                        this.client.send("E_ERROR");
                        this.client.close();
                        return;
                    }
                }
                String get = endtradesother.get(tradeAccount);
                String get1 = endtradesother.get(tradeWithAccount);
                RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
                RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
                String get2 = startedtrades.get(tradeAccount);
                String get3 = startedtrades.get(tradeWithAccount);
                if(get2.startsWith("btc2btcry")&&get3.startsWith("btc2btcry")||get2.startsWith("btcry2btc")&&get3.startsWith("btcry2btc"))
                {
                     try {
                        this.client.send("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                        this.client.send("E_ERROR");
                        this.client.close();
                        return;
                    }
                }
                if(get2.startsWith("btc2btcry")&&get3.startsWith("btcry2btc")) {
                    String signrawtransaction_multisig = bitcoinrpc.signrawtransaction_multisig(get);
                    String signrawtransaction_multisig1 = bitcrystalrpc.signrawtransaction_multisig(get1);
                    JsonObject decodeRawTransactionMultisig = bitcoinrpc.decodeRawTransactionMultisig(signrawtransaction_multisig);
                    JsonObject decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                    Object[] values = {signrawtransaction_multisig};
                    Object[] values2 = {signrawtransaction_multisig1};
                    if(decodeRawTransactionMultisig.get("complete").getAsBoolean()==true&&decodeRawTransactionMultisig1.get("complete").getAsBoolean()==true) {
                        bitcoinrpc.sendrawtransaction_multisig(values);
                        bitcrystalrpc.sendrawtransaction_multisig(values2);
                    }
                } else if (get2.startsWith("btcry2btc")&&get3.startsWith("btc2btcry")) {
                    String signrawtransaction_multisig = bitcoinrpc.signrawtransaction_multisig(get1);
                    String signrawtransaction_multisig1 = bitcrystalrpc.signrawtransaction_multisig(get);
                    JsonObject decodeRawTransactionMultisig = bitcoinrpc.decodeRawTransactionMultisig(signrawtransaction_multisig);
                    JsonObject decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                    Object[] values = {signrawtransaction_multisig};
                    Object[] values2 = {signrawtransaction_multisig1};
                    if(decodeRawTransactionMultisig.get("complete").getAsBoolean()==true&&decodeRawTransactionMultisig1.get("complete").getAsBoolean()==true) {
                        bitcoinrpc.sendrawtransaction_multisig(values);
                        bitcrystalrpc.sendrawtransaction_multisig(values2);
                    }
                } else {
                    try {
                        this.client.send("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                        this.client.send("E_ERROR");
                        this.client.close();
                        return;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.send("E_ERROR");
                this.client.close();
                return;
            }
        }
    }
}
