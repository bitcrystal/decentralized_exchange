/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.google.gson.JsonObject;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.security.krb5.internal.HostAddress;

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
    private static JSONObject serverJSON = null;
    private static boolean isInit = false;

    public ServerConnection(TCPClient client) {
        this.client = DecentralizedExchange.getSecurityClient(client);
        if (!isInit) {
            initServer();
        } else {
            saveServer();
        }
    }

    public void run() {
        String recv = "";
        try {
            recv = this.client.recv();
        } catch (Exception ex) {
            recv = "";
        }
        if (recv == null || recv.isEmpty()) {
            this.client.close();
            return;
        }
        if (recv.equals("E_ERROR")) {
            DebugServer.println("E_ERROR");
            this.client.close();
            return;
        }
        if (recv.startsWith("add,")) {
            DebugServer.println("add open");
            add(recv);
            DebugServer.println("add close");
            this.saveServer();
            return;
        }

        if (recv.startsWith("tradewith,")) {
            DebugServer.println("tradewith open");
            tradewith(recv);
            DebugServer.println("tradewith close");
            this.saveServer();
            return;
        }


        if (recv.startsWith("synctrade;")) {
            DebugServer.println("synctrade open");
            synctrade(recv);
            DebugServer.println("synctrade close");
            this.saveServer();
            return;
        }

        if (recv.startsWith("starttrade")) {
            DebugServer.println("starttrade open");
            starttrade();
            DebugServer.println("starttrade close");
            this.saveServer();
            return;
        }

        if (recv.startsWith("endtrademe;")) {
            DebugServer.println("endtrademe open");
            endtrademe(recv);
            DebugServer.println("endtrademe close");
            this.saveServer();
            return;
        }

        if (recv.startsWith("endtradeother;")) {
            DebugServer.println("endtradeother open");
            endtradeother(recv);
            DebugServer.println("endtradeother close");
            this.saveServer();
            return;
        }

        if (recv.startsWith("endtrade")) {
            DebugServer.println("endtrade open");
            endtrade();
            DebugServer.println("endtrade close");
            this.saveServer();
            return;
        }

        if (recv.startsWith("tradeabort;;")) {
            DebugServer.println("tradeabort open");
            tradeabort(recv);
            DebugServer.println("tradeabort close");
            this.saveServer();
            return;
        }
    }

    private void starttrade() {
        DebugServer.println("serverconnection@362");
        String hostAddress = this.client.getSocket().getInetAddress().getHostAddress();
        if (!syncedtrades.containsKey(hostAddress)) {
            DebugServer.println("serverconnection@364");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }

        String get = ips.get(hostAddress);
        DebugServer.println(get);
        DebugServer.println("serverconnection@380");
        if (!syncedtrades.containsKey(get)) {
            DebugServer.println("serverconnection@383");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        String get1 = syncedtrades.get(hostAddress);
        String get2 = syncedtrades.get(get);
        DebugServer.println(get1);
        DebugServer.println(get2);
        DebugServer.println("serverconnection@400");
        if (!get1.contains(",,") || !get2.contains(",,")) {
            DebugServer.println("serverconnection@402");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        if (!get1.startsWith("btc2btcry") && !get1.startsWith("btcry2btc")) {
            DebugServer.println("serverconnection@416");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        if (!get2.startsWith("btc2btcry") && !get2.startsWith("btcry2btc")) {
            DebugServer.println("serverconnection@430");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        String[] split = get1.split(",,");
        String[] split2 = get2.split(",,");
        if (split.length != split2.length) {
            DebugServer.println("serverconnection@446");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        if (split.length != 5) {
            DebugServer.println("serverconnection@459");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }

        if (!tradeAccounts.contains(split[3]) || !tradeAccounts.contains(split[4]) || !tradeAccounts.contains(split2[3]) || !tradeAccounts.contains(split2[4])) {
            DebugServer.println("serverconnection@474");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        DebugServer.println(split[0]);
        DebugServer.println(split[2]);
        DebugServer.println("split[0] == split[2] ?");
        /*if (split[0].equals(split2[0])) {
        DebugServer.println("serverconnection@488");
        try {
        this.client.sendLight("E_ERROR");
        Thread.sleep(3000L);
        this.client.close();
        return;
        } catch (InterruptedException ex) {
        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        this.client.sendLight("E_ERROR");
        this.client.close();
        return;
        }
        }*/
        DebugServer.println(split[1]);
        DebugServer.println(split2[2]);
        if (!(split[1].equals(split2[2]) && split2[1].equals(split[2]))) {
            DebugServer.println("serverconnection@502");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }

        if (!(split[3].equals(split2[4]) && split2[3].equals(split[4]))) {
            DebugServer.println("serverconnection@518");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }

        String tradeAccount = split[3];
        String tradeWithAccount = split[4];
        String tradeAccount2 = split2[3];
        String tradeWithAccount2 = split2[4];
        if (!tradeAccount.contains(",") || !tradeWithAccount.contains(",") || !tradeAccount2.contains(",") || !tradeWithAccount2.contains(",")) {
            DebugServer.println("serverconnection@537");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        if (startedtrades.containsKey(tradeAccount) || startedtrades.containsKey(tradeAccount2) || startedtrades.containsKey(tradeWithAccount) || startedtrades.containsKey(tradeWithAccount2)) {
            DebugServer.println("serverconnection@550");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }

        String get3 = ipsTradeAccounts.get(tradeAccount);
        String get4 = ipsTradeAccounts2.get(tradeAccount2);
        /* if ((!get3.equals(hostAddress) && !get4.equals(hostAddress)) || (get3.equals(hostAddress) && get4.equals(hostAddress))) {
        DebugServer.println("serverconnection@568");
        try {
        this.client.sendLight("E_ERROR");
        Thread.sleep(3000L);
        this.client.close();
        return;
        } catch (InterruptedException ex) {
        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        this.client.sendLight("E_ERROR");
        this.client.close();
        return;
        }
        }*/
        String[] tradeAccountAddresses = split[3].split(",");
        String[] tradeWithAccountAddresses = split[4].split(",");
        String[] tradeAccount2Addresses = split2[3].split(",");
        String[] tradeWithAccount2Addresses = split2[4].split(",");
        if (tradeAccountAddresses.length != 2 || tradeWithAccountAddresses.length != 2 || tradeAccount2Addresses.length != 2 || tradeWithAccount2Addresses.length != 2) {
            DebugServer.println("serverconnection@585");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        double split_amount = 0;
        double split_price = 0;
        double split2_amount = 0;
        double split2_price = 0;
        try {
            DebugServer.println("serverconnection@604");
            split_amount = Double.parseDouble(split[1]);
            split_price = Double.parseDouble(split[2]);
            split2_amount = Double.parseDouble(split2[1]);
            split2_price = Double.parseDouble(split2[2]);
            if (split_amount <= 0 || split_price <= 0 || split2_amount <= 0 || split2_price <= 0) {
                DebugServer.println("serverconnection@610");
                throw new Exception();
            }
            if (split_amount != split2_price || split2_amount != split_price) {
                DebugServer.println("serverconnection@614");
                throw new Exception();
            }
        } catch (Exception ex2) {
            DebugServer.println("serverconnection@618");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }


        if (split[0].equals("btc2btcry") || split2[0].equals("btcry2btc")) {
            DebugServer.println("serverconnection@634");
            try {
                DebugServer.println("serverconnection@636");
                RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
                RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
                Object[] values = {tradeAccount, tradeAccountAddresses[1], split_price, 0.00};
                Object[] values2 = {tradeWithAccount, tradeAccountAddresses[0], split_amount, 0.00};
                DebugServer.println(split_price);
                DebugServer.println(split_amount);
                DebugServer.println("serverconnection@643");
                if (bitcoinrpc.getBalance(tradeAccount) < split_price + 0.00000001) {
                    DebugServer.println("serverconnection@642");
                    try {
                        this.client.sendLight("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                        this.client.sendLight("E_ERROR");
                        this.client.close();
                        return;
                    }
                }
                if (bitcrystalrpc.getBalance(tradeWithAccount) < split_amount + 0.00000001) {
                    DebugServer.println("serverconnection@659");
                    try {
                        this.client.sendLight("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                        this.client.sendLight("E_ERROR");
                        this.client.close();
                        return;
                    }
                }
                String createrawtransaction_multisig = bitcoinrpc.createrawtransaction_multisig(values);
                String createrawtransaction_multisig1 = bitcrystalrpc.createrawtransaction_multisig(values2);
                String tradeAccountSend = "btc2btcry;;" + values[0] + ";;" + values[1] + ";;" + values[2] + ";;" + createrawtransaction_multisig;
                String tradeWithAccountSend = "btcry2btc;;" + values2[0] + ";;" + values2[1] + ";;" + values2[2] + ";;" + createrawtransaction_multisig1;
                DebugServer.println(tradeAccountSend);
                DebugServer.println(tradeWithAccountSend);
                DebugServer.println(createrawtransaction_multisig);
                DebugServer.println(createrawtransaction_multisig1);
                DebugServer.println("serverconnection@680");
                startedtrades.put(tradeAccount, tradeAccountSend);
                //startedtrades.put(tradeWithAccount, tradeWithAccountSend);
                startedtradesaccount.put(tradeAccount, tradeWithAccount);
                //startedtradesaccount.put(tradeWithAccount, tradeAccount);
                client.send("ALL_OK");
                DebugServer.println("TRADE IS STARTED");
                client.recv();
                DebugServer.println("WORKED AWESOME!");
                client.close();
                return;
            } catch (Exception ex) {
                DebugServer.println("serverconnection@687");
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }

        if (split[0].equals("btcry2btc") && split2[0].equals("btc2btcry")) {
            DebugServer.println("serverconnection@696");
            try {
                DebugServer.println("serverconnection@698");
                RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
                RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
                Object[] values = {tradeAccount, tradeAccountAddresses[1], split_price};
                Object[] values2 = {tradeWithAccount, tradeAccountAddresses[0], split_amount};
                if (bitcrystalrpc.getBalance(tradeAccount) < split_price + 0.00000001) {
                    DebugServer.println("serverconnection@704");
                    try {
                        this.client.sendLight("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                        this.client.sendLight("E_ERROR");
                        this.client.close();
                        return;
                    }
                }
                if (bitcoinrpc.getBalance(tradeWithAccount) < split_amount + 0.00000001) {
                    DebugServer.println("serverconnection@718");
                    try {
                        this.client.sendLight("E_ERROR");
                        Thread.sleep(3000L);
                        this.client.close();
                        return;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                        this.client.sendLight("E_ERROR");
                        this.client.close();
                        return;
                    }
                }
                String createrawtransaction_multisig = bitcrystalrpc.createrawtransaction_multisig(values);
                String createrawtransaction_multisig1 = bitcoinrpc.createrawtransaction_multisig(values2);
                String tradeAccountSend = "btcry2btc;;" + values[0] + ";;" + values[1] + ";;" + values[2] + ";;" + createrawtransaction_multisig;
                String tradeWithAccountSend = "btc2btcry;;" + values2[0] + ";;" + values2[1] + ";;" + values2[2] + ";;" + createrawtransaction_multisig1;
                startedtrades.put(tradeAccount, tradeAccountSend);
                //startedtrades.put(tradeWithAccount, tradeWithAccountSend);
                startedtradesaccount.put(tradeAccount, tradeWithAccount);
                //startedtradesaccount.put(tradeWithAccount, tradeAccount);
                DebugServer.println("serverconnection@739");
                client.send("ALL_OK");
                DebugServer.println("TRADE IS STARTED");
                client.recv();
                DebugServer.println("WORKED AWESOME");
                client.close();
            } catch (Exception ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        return;
    }

    private void endtrade() {
        DebugServer.println("serverconnection@877");
        String tradeAccount = "";
        String tradeWithAccount = "";
        String hostAddress = "";
        String otherip = "";
        try {
            hostAddress = this.client.getHostAddress();
            if (!ips.containsKey(hostAddress)) {
                DebugServer.println("serverconnection@881");
                try {
                    this.client.sendLight("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.sendLight("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            otherip = ips.get(hostAddress);
            if (!ips.containsKey(otherip)) {
                DebugServer.println("serverconnection@881");
                try {
                    this.client.sendLight("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.sendLight("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            if (tradeAccountsIp.containsKey(hostAddress)) {
                DebugServer.println("serverconnection@887");
                tradeAccount = tradeAccountsIp.get(hostAddress);
            } else if (tradeAccountsIp2.containsKey(hostAddress)) {
                DebugServer.println("serverconnection@889");
                tradeAccount = tradeAccountsIp2.get(hostAddress);
            }

            if (tradeAccountsIp.containsKey(otherip)) {
                DebugServer.println("serverconnection@895");
                tradeWithAccount = tradeAccountsIp.get(otherip);
            } else if (tradeAccountsIp2.containsKey(otherip)) {
                DebugServer.println("serverconnection@898");
                tradeWithAccount = tradeAccountsIp2.get(otherip);
            }

            if (tradeAccount.isEmpty() || tradeWithAccount.isEmpty()) {
                DebugServer.println("serverconnection@904");
                try {
                    this.client.sendLight("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.sendLight("E_ERROR");
                    this.client.close();
                    return;
                }
            }
            if (!endtradesother.containsKey(tradeAccount) || !endtradesother.containsKey(tradeWithAccount)) {
                DebugServer.println("serverconnection@917");
                try {
                    this.client.sendLight("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.sendLight("E_ERROR");
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
            /*if (get2.startsWith("btc2btcry") && get3.startsWith("btc2btcry") || get2.startsWith("btcry2btc") && get3.startsWith("btcry2btc")) {
            DebugServer.println("serverconnection@937");
            try {
            this.client.sendLight("E_ERROR");
            Thread.sleep(3000L);
            this.client.close();
            return;
            } catch (InterruptedException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            this.client.sendLight("E_ERROR");
            this.client.close();
            return;
            }
            }*/
            if (get2.startsWith("btc2btcry") || get3.startsWith("btcry2btc")) {
                DebugServer.println("serverconnection@951");
                String signrawtransaction_multisig = bitcoinrpc.signrawtransaction_multisig(get, 1);
                String signrawtransaction_multisig1 = bitcrystalrpc.signrawtransaction_multisig(get1, 1);
                JsonObject decodeRawTransactionMultisig = bitcoinrpc.decodeRawTransactionMultisig(signrawtransaction_multisig);
                JsonObject decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                Object[] values = {signrawtransaction_multisig};
                Object[] values2 = {signrawtransaction_multisig1};
                if (decodeRawTransactionMultisig.get("complete").getAsBoolean() == true && decodeRawTransactionMultisig1.get("complete").getAsBoolean() == true) {
                    DebugServer.println("serverconnection@959");
                    bitcoinrpc.sendrawtransaction_multisig(values);
                    bitcrystalrpc.sendrawtransaction_multisig(values2);
                }
            } else if (get2.startsWith("btcry2btc") && get3.startsWith("btc2btcry")) {
                DebugServer.println("serverconnection@964");
                String signrawtransaction_multisig = bitcoinrpc.signrawtransaction_multisig(get1, 1);
                String signrawtransaction_multisig1 = bitcrystalrpc.signrawtransaction_multisig(get, 1);
                JsonObject decodeRawTransactionMultisig = bitcoinrpc.decodeRawTransactionMultisig(signrawtransaction_multisig);
                JsonObject decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                Object[] values = {signrawtransaction_multisig};
                Object[] values2 = {signrawtransaction_multisig1};
                if (decodeRawTransactionMultisig.get("complete").getAsBoolean() == true && decodeRawTransactionMultisig1.get("complete").getAsBoolean() == true) {
                    DebugServer.println("serverconnection@972");
                    bitcoinrpc.sendrawtransaction_multisig(values);
                    bitcrystalrpc.sendrawtransaction_multisig(values2);
                }
            } else {
                DebugServer.println("serverconnection@988");
                try {
                    this.client.sendLight("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    this.client.sendLight("E_ERROR");
                    this.client.close();
                    return;
                }
            }
        } catch (Exception ex) {
            DebugServer.println("serverconnection@991");
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            this.client.sendLight("E_ERROR");
            this.client.close();
            return;
        }
        endtradesme.remove(tradeAccount);
        endtradesme.remove(tradeWithAccount);
        endtradesother.remove(tradeAccount);
        endtradesother.remove(tradeWithAccount);
        startedtrades.remove(tradeAccount);
        startedtrades.remove(tradeWithAccount);
        startedtradesaccount.remove(tradeAccount);
        startedtradesaccount.remove(tradeWithAccount);
        syncedtrades.remove(hostAddress);
        syncedtrades.remove(otherip);
        this.client.sendLight("ALL_OK");
        DebugServer.println("ENDTRADE WORKED YEAH! MOTHERFUCKER");
        return;
    }

    private void endtrademe(String recv) {
        DebugServer.println("serverconnection@754");
        String[] split = recv.split(";");
        if (split.length != 2) {
            DebugServer.println("serverconnection@757");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        if (!startedtrades.containsKey(split[1])) {
            DebugServer.println("serverconnection@771");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        if (endtradesme.containsKey(split[1])) {
            DebugServer.println("serverconnection@785");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }

        this.client.sendLight(startedtrades.get(split[1]));
        DebugServer.println("serverconnection@800");
        String recv1 = this.client.recvLight();
        DebugServer.println(recv1);
        DebugServer.println("serverconnection@802");
        if (recv1.equals("E_ERROR")) {
            DebugServer.println("serverconnection@805");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        DebugServer.println("serverconnection@819");
        endtradesme.put(split[1], recv1);
        DebugServer.println("TRADE IS ENDED");
        this.client.sendLight("ALL_OK");
        this.client.close();
        return;
    }

    private void endtradeother(String recv) {
        DebugServer.println("serverconnection@827");
        String[] split = recv.split(";");
        if (split.length != 2) {
            DebugServer.println("serverconnection@830");
            this.client.sendLight("E_ERROR");
            this.client.close();
            return;
        }
        if (!startedtradesaccount.containsKey(split[1])) {
            DebugServer.println("serverconnection@836");
            this.client.sendLight("E_ERROR");
            this.client.close();
            return;
        }
        String get = startedtradesaccount.get(split[1]);
        if (!endtradesme.containsKey(get) || !endtradesme.containsKey(split[1])) {
            DebugServer.println("serverconnection@843");
            this.client.sendLight("E_ERROR");
            this.client.close();
            return;
        }
        String get1 = endtradesme.get(get);
        DebugServer.println("serverconnection@848");
        this.client.sendLight(get1);
        DebugServer.println("serverconnection@851");
        String recv2 = this.client.recvLight();
        DebugServer.println(recv2);
        DebugServer.println("serverconnection@853");
        if (recv2.equals("E_ERROR")) {
            DebugServer.println("serverconnection@856");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
                return;
            }
        }
        DebugServer.println("serverconnection@870");
        endtradesother.put(get, recv2);
        DebugServer.println("ENDTRADEOTHER GREAT!");
        this.client.sendLight("ALL_OK");
        this.client.close();
        return;
    }

    private synchronized void initServer() {
        isInit = true;
        serverJSON = this.client.loadJSONObject("server", "", "server.properties");
        if (serverJSON == null) {
            try {
                JSONObject json = new JSONObject();
                json.put("pubKeys", listToJSONArray(pubKeys));
                json.put("addressesPubkeys", mapToJSONObject(addressesPubkeys));
                json.put("pubkeysAddresses", mapToJSONObject(pubkeysAddresses));
                json.put("pubKeysMap", mapToJSONObject(pubKeysMap));
                json.put("pubKeysMap2", mapToJSONObject(pubKeysMap2));
                json.put("serverAddressesPubkeys", mapToJSONObject(serverAddressesPubkeys));
                json.put("serverPubkeysAddresses", mapToJSONObject(serverPubkeysAddresses));
                json.put("serverPubKeys", listToJSONArray(serverPubKeys));
                json.put("ipsTradeAccounts", mapToJSONObject(ipsTradeAccounts));
                json.put("ipsTradeAccounts2", mapToJSONObject(ipsTradeAccounts2));
                json.put("tradeAccountsIp", mapToJSONObject(tradeAccountsIp));
                json.put("tradeAccountsIp2", mapToJSONObject(tradeAccountsIp2));
                json.put("ips", mapToJSONObject(ips));
                json.put("addresses", mapToJSONObject(addresses));
                json.put("syncedtrades", mapToJSONObject(startedtrades));
                json.put("startedtrades", mapToJSONObject(startedtrades));
                json.put("endtradesme", mapToJSONObject(endtradesme));
                json.put("endtradesother", mapToJSONObject(endtradesother));
                json.put("startedtradesaccount", mapToJSONObject(startedtradesaccount));
                DebugServer.println(json.toString());
                this.client.saveJSONObject(json, "server", "", "server.properties");
                serverJSON = json;
            } catch (JSONException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                pubKeys = JSONArrayToList(serverJSON.get("pubKeys"));
            } catch (JSONException ex) {
                pubKeys = new CopyOnWriteArrayList<String>();
            }
            try {
                addressesPubkeys = jsonObjectToMap(serverJSON.get("addressesPubkeys"));
            } catch (JSONException ex) {
                addressesPubkeys = new ConcurrentHashMap<String, String>();
            }
            try {
                pubkeysAddresses = jsonObjectToMap(serverJSON.get("pubkeysAddresses"));
            } catch (JSONException ex) {
                pubkeysAddresses = new ConcurrentHashMap<String, String>();
            }
            try {
                pubKeysMap = jsonObjectToMap(serverJSON.get("pubKeysMap"));
            } catch (JSONException ex) {
                pubKeysMap = new ConcurrentHashMap<String, String>();
            }
            try {
                pubKeysMap2 = jsonObjectToMap(serverJSON.get("pubKeysMap2"));
            } catch (JSONException ex) {
                pubKeysMap2 = new ConcurrentHashMap<String, String>();
            }
            try {
                serverAddressesPubkeys = jsonObjectToMap(serverJSON.get("serverAddressesPubkeys"));
            } catch (JSONException ex) {
                serverAddressesPubkeys = new ConcurrentHashMap<String, String>();
            }
            try {
                serverPubkeysAddresses = jsonObjectToMap(serverJSON.get("serverPubkeysAddresses"));
            } catch (JSONException ex) {
                serverPubkeysAddresses = new ConcurrentHashMap<String, String>();
            }
            try {
                serverPubKeys = JSONArrayToList(serverJSON.get("serverPubKeys"));
            } catch (JSONException ex) {
                serverPubKeys = new CopyOnWriteArrayList<String>();
            }
            try {
                tradeAccounts = JSONArrayToList(serverJSON.get("tradeAccounts"));
            } catch (JSONException ex) {
                tradeAccounts = new CopyOnWriteArrayList<String>();
            }
            try {
                ipsTradeAccounts = jsonObjectToMap(serverJSON.get("ipsTradeAccounts"));
            } catch (JSONException ex) {
                ipsTradeAccounts = new ConcurrentHashMap<String, String>();
            }
            try {
                ipsTradeAccounts2 = jsonObjectToMap(serverJSON.get("ipsTradeAccounts2"));
            } catch (JSONException ex) {
                ipsTradeAccounts2 = new ConcurrentHashMap<String, String>();
            }

            try {
                tradeAccountsIp = jsonObjectToMap(serverJSON.get("tradeAccountsIp"));
            } catch (JSONException ex) {
                tradeAccountsIp = new ConcurrentHashMap<String, String>();
            }
            try {
                tradeAccountsIp2 = jsonObjectToMap(serverJSON.get("tradeAccountsIp2"));
            } catch (JSONException ex) {
                tradeAccountsIp2 = new ConcurrentHashMap<String, String>();
            }
            try {
                ips = jsonObjectToMap(serverJSON.get("ips"));
            } catch (JSONException ex) {
                ips = new ConcurrentHashMap<String, String>();
            }
            try {
                addresses = jsonObjectToMap(serverJSON.get("addresses"));
            } catch (JSONException ex) {
                addresses = new ConcurrentHashMap<String, String>();
            }
            try {
                syncedtrades = jsonObjectToMap(serverJSON.get("syncedtrades"));
            } catch (JSONException ex) {
                syncedtrades = new ConcurrentHashMap<String, String>();
            }
            try {
                startedtrades = jsonObjectToMap(serverJSON.get("startedtrades"));
            } catch (JSONException ex) {
                startedtrades = new ConcurrentHashMap<String, String>();
            }
            try {
                endtradesme = jsonObjectToMap(serverJSON.get("endtradesme"));
            } catch (JSONException ex) {
                endtradesme = new ConcurrentHashMap<String, String>();
            }
            try {
                endtradesother = jsonObjectToMap(serverJSON.get("endtradesother"));
            } catch (JSONException ex) {
                endtradesother = new ConcurrentHashMap<String, String>();
            }
            try {
                startedtradesaccount = jsonObjectToMap(serverJSON.get("startedtradesaccount"));
            } catch (JSONException ex) {
                startedtradesaccount = new ConcurrentHashMap<String, String>();
            }
        }
    }

    private synchronized void saveServer() {
        JSONObject json = this.client.loadJSONObject("server", "", "server.properties");
        if (json == null) {
            return;
        }
        try {
            json.put("pubKeys", listToJSONArray(pubKeys));
            json.put("addressesPubkeys", mapToJSONObject(addressesPubkeys));
            json.put("pubkeysAddresses", mapToJSONObject(pubkeysAddresses));
            json.put("pubKeysMap", mapToJSONObject(pubKeysMap));
            json.put("pubKeysMap2", mapToJSONObject(pubKeysMap2));
            json.put("serverAddressesPubkeys", mapToJSONObject(serverAddressesPubkeys));
            json.put("serverPubkeysAddresses", mapToJSONObject(serverPubkeysAddresses));
            json.put("serverPubKeys", listToJSONArray(serverPubKeys));
            json.put("ipsTradeAccounts", mapToJSONObject(ipsTradeAccounts));
            json.put("ipsTradeAccounts2", mapToJSONObject(ipsTradeAccounts2));
            json.put("tradeAccountsIp", mapToJSONObject(tradeAccountsIp));
            json.put("tradeAccountsIp2", mapToJSONObject(tradeAccountsIp2));
            json.put("ips", mapToJSONObject(ips));
            json.put("addresses", mapToJSONObject(addresses));
            json.put("syncedtrades", mapToJSONObject(startedtrades));
            json.put("startedtrades", mapToJSONObject(startedtrades));
            json.put("endtradesme", mapToJSONObject(endtradesme));
            json.put("endtradesother", mapToJSONObject(endtradesother));
            json.put("startedtradesaccount", mapToJSONObject(startedtradesaccount));
            DebugServer.println(json.toString());
            this.client.saveJSONObject(json, "server", "", "server.properties");
            serverJSON = json;
        } catch (JSONException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void synctrade(String recv) {
        String hostAddress = this.client.getSocket().getInetAddress().getHostAddress();
        DebugServer.println(hostAddress);
        if (syncedtrades.containsKey(hostAddress)) {
            DebugServer.println("serverconnection@261");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
            } catch (InterruptedException ex) {
                DebugServer.println("serverconnection@267");
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
            }
            return;
        }
        DebugServer.println("serverconnection@273");
        if (!tradeAccountsIp.containsKey(hostAddress) && !tradeAccountsIp2.containsKey(hostAddress)) {
            DebugServer.println("serverconnection@275");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
            }
            return;
        }
        DebugServer.println("serverconnection@286");
        String[] split = recv.split(";");
        if (split.length != 2) {
            DebugServer.println("serverconnection@289");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
            }
            return;
        }
        if (!ips.containsKey(hostAddress)) {
            DebugServer.println("serverconnection@302");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
            }
            return;
        }
        String get = ips.get(hostAddress);
        DebugServer.println("serverconnection@314");
        if (!ips.containsKey(get)) {
            DebugServer.println("serverconnection@316");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
            }
            return;
        }
        String get1 = ips.get(get);
        DebugServer.println(get1);
        DebugServer.println("serverconnection@328");
        if (!get1.equals(hostAddress)) {
            DebugServer.println("serverconnection@331");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
            }
            return;
        }
        if (!addresses.containsKey(hostAddress) || !addresses.containsKey(get)) {
            DebugServer.println("serverconnection@343");
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                this.client.close();
            }
            return;
        }
        this.syncedtrades.put(hostAddress, split[1]);
        this.client.sendLight("TRADE IS SYNCED");
        DebugServer.println("TRADE IS SYNCED");
        //this.client.recv();
        this.client.close();
        return;
    }

    private void tradewith(String recv) {
        DebugServer.println("serverconnection@114");
        String hostAddress = this.client.getSocket().getInetAddress().getHostAddress();
        try {
            if (ips.containsKey(hostAddress)) {
                DebugServer.println("serverconnection@117");
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            }

            String[] split = recv.split(",");
            if (split[1].contains(".")) {
                if (!pubKeysMap.containsKey(split[1]) || split[1].equalsIgnoreCase(hostAddress)) {
                    DebugServer.println("serverconnection@128");
                    this.client.sendLight("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                }
                DebugServer.println("serverconnection@134");
                split[1] = pubKeysMap.get(split[1]);
            }
            if (addressesPubkeys.containsKey(split[1])) {
                DebugServer.println("serverconnection@136");
                split[1] = addressesPubkeys.get(split[1]);
            }

            if (!pubKeys.contains(split[1])) {
                DebugServer.println("serverconnection@140");
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } else {
                if (!pubKeysMap2.containsKey(split[1])) {
                    DebugServer.println("serverconnection@146");
                    this.client.sendLight("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                }
                String get10 = pubKeysMap2.get(split[1]);
                DebugServer.println("get10");
                DebugServer.println(get10);
                if (!pubKeysMap.containsKey(get10)) {
                    DebugServer.println("serverconnection@155");
                    this.client.sendLight("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                }
                /*if (get10.equals(hostAddress)) {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
                }*/
                String get11 = pubKeysMap.get(get10);
                DebugServer.println("get11");
                DebugServer.println(get11);
                split[1] = get11;
                DebugServer.println("split[1] = get11");
                DebugServer.println(split[1]);
            }
            if (addresses.containsKey(hostAddress)) {
                DebugServer.println("serverconnection@174");
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            }
            if (!pubKeysMap.containsKey(hostAddress)) {
                DebugServer.println("serverconnection@180");
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            }
            if (!pubKeysMap2.containsKey(split[1])) {
                DebugServer.println("serverconnection@186");
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            }
            String get = pubKeysMap2.get(split[1]);
            DebugServer.println("get");
            DebugServer.println(get);
            /*if (get.equals(hostAddress)) {
            this.client.sendLight("E_ERROR");
            Thread.sleep(3000L);
            this.client.close();
            return;
            }*/
            if (pubKeys.size() <= 0) {
                DebugServer.println("serverconnection@201");
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            }
            String get1 = serverPubKeys.get(serverPubKeys.size() - 1);
            serverPubKeys.remove(serverPubKeys.size() - 1);
            String get2 = pubKeysMap.get(hostAddress);
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            String ba = bitcoinrpc.getBitcoinAddressOfPubKey(get2);
            addresses.put(hostAddress, ba);
            String ba2 = bitcoinrpc.getBitcoinAddressOfPubKey(split[1]);
            String account = ba + "," + ba2;
            String account2 = ba2 + "," + ba;
            Object[] values = {get2, split[1], get1, true};
            String createmultisigaddressex = bitcoinrpc.createmultisigaddressex(values);
            String createmultisigaddressex2 = bitcrystalrpc.createmultisigaddressex(values);
            DebugServer.println("serverconnection@217");
            this.client.sendLight(createmultisigaddressex + ",," + createmultisigaddressex2 + ",," + ba2);
            this.client.close();
            Object[] values2 = {createmultisigaddressex, account};
            Object[] values3 = {createmultisigaddressex2, account};
            Object[] values4 = {createmultisigaddressex, account2};
            Object[] values5 = {createmultisigaddressex2, account2};
            if (!tradeAccounts.contains(account)) {
                DebugServer.println("serverconnection@224");
                bitcoinrpc.addmultisigaddressex(values2);
                bitcrystalrpc.addmultisigaddressex(values3);
                tradeAccounts.add(account);
                ipsTradeAccounts.put(account, hostAddress);
                ipsTradeAccounts2.put(account, get);
                tradeAccountsIp.put(hostAddress, account);
                tradeAccountsIp.put(get, account);
            }
            if (!tradeAccounts.contains(account2)) {
                DebugServer.println("serverconnection@233");
                bitcoinrpc.addmultisigaddressex(values4);
                bitcrystalrpc.addmultisigaddressex(values5);
                tradeAccounts.add(account2);
                ipsTradeAccounts.put(account2, hostAddress);
                ipsTradeAccounts2.put(account2, get);
                tradeAccountsIp2.put(hostAddress, account2);
                tradeAccountsIp2.put(get, account2);
            }
            DebugServer.println("serverconnection@241");
            ips.put(hostAddress, get);
            return;
        } catch (Exception ex) {
            DebugServer.println("serverconnection@244");
            try {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
            } catch (InterruptedException ex1) {
                this.client.sendLight("E_ERROR");
                this.client.close();
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return;
        }
    }

    private void add(String recv) {
        String hostAddress = this.client.getSocket().getInetAddress().getHostAddress();
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            String[] split = recv.split(",");
            boolean isPubKey = bitcoinrpc.isValidPubKey(split[1]);
            if (!isPubKey) {
                DebugServer.println("serverconnection@67");
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
            }
            if (!pubKeys.contains(split[1])) {
                DebugServer.println("serverconnection@72");
                String bitcoinAddressOfPubKey = bitcoinrpc.getBitcoinAddressOfPubKey(split[1]);
                addressesPubkeys.put(bitcoinAddressOfPubKey, split[1]);
                pubkeysAddresses.put(split[1], bitcoinAddressOfPubKey);
                pubKeys.add(split[1]);
                pubKeysMap.put(hostAddress, split[1]);
                pubKeysMap2.put(split[1], hostAddress);
                String newAddress = bitcoinrpc.getNewAddress();
                String pubKey = bitcoinrpc.getPubKey(newAddress);
                String privKey = bitcoinrpc.getPrivKey(newAddress);
                if (!bitcrystalrpc.addressexists(newAddress)) {
                    DebugServer.println("serverconnection@83");
                    bitcrystalrpc.importPrivKey(privKey);
                }
                serverPubKeys.add(pubKey);
                serverAddressesPubkeys.put(newAddress, pubKey);
                serverPubkeysAddresses.put(pubKey, newAddress);
                this.client.sendLight("ALL_OK");
                Thread.sleep(3000L);
                this.client.close();
                return;
            }
            this.client.sendLight("E_ERROR");
            Thread.sleep(3000L);
            this.client.close();
            return;

        } catch (Exception ex) {
            DebugServer.println("serverconnection@98");
            try {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex1) {
                DebugServer.println("serverconnection@105");
                this.client.sendLight("E_ERROR");
                this.client.close();
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
                return;
            }
        }
    }

    private static List<String> JSONArrayToList(Object object) {
        if (!(object instanceof JSONArray)) {
            return null;
        }
        JSONArray array = (JSONArray) object;
        int length = array.length();
        List<String> list = new CopyOnWriteArrayList<String>();
        for (int i = 0; i < length; i++) {
            try {
                list.add(array.getString(i));
            } catch (JSONException ex) {
                continue;
            }
        }
        return list;
    }

    private static JSONArray listToJSONArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return new JSONArray();
        }

        JSONArray jsonArray = new JSONArray();
        int length = list.size();
        for (int i = 0; i < length; i++) {
            jsonArray.put(list.get(i));
        }
        return jsonArray;
    }

    private static JSONObject mapToJSONObject(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return new JSONObject();
        }

        JSONObject jsonObject = new JSONObject();
        Set<String> keys = new HashSet<String>(map.keySet());
        for (String key : keys) {
            try {
                jsonObject.put(key, map.get(key));
            } catch (Exception ex) {
                continue;
            }
        }
        return jsonObject;
    }

    private static Map<String, String> jsonObjectToMap(Object object) {
        JSONObject jsonObject = null;
        if (!(object instanceof JSONObject)) {
            if (object instanceof String) {
                try {
                    jsonObject = new JSONObject(object.toString());
                } catch (JSONException ex) {
                    jsonObject = null;
                }
            } else {
                jsonObject = null;
            }
        } else {
            jsonObject = (JSONObject) object;
        }

        if (jsonObject == null) {
            return new ConcurrentHashMap<String, String>();
        }
        JSONArray names = jsonObject.names();
        if (names == null) {
            return new ConcurrentHashMap<String, String>();
        }
        int length = names.length();
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        for (int i = 0; i < length; i++) {
            try {
                String key = names.getString(i);
                map.put(key, jsonObject.getString(key));
            } catch (JSONException ex) {
                continue;
            }
        }
        return map;
    }

    private void tradeabort(String recv) {
        if (recv.contains(";;")) {
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex1) {
                DebugServer.println("serverconnection@105");
                this.client.sendLight("E_ERROR");
                this.client.close();
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
                return;
            }
        }
        String[] split = recv.split(";;");
        if (split.length != 2) {
            try {
                DebugServer.println("serverconnection@1394");
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex1) {
                DebugServer.println("serverconnection@105");
                this.client.sendLight("E_ERROR");
                this.client.close();
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
                return;
            }
        }

        /*if (!HashFunctions.isValidProofOfWorkString(split[2])) {
        try {
        DebugServer.println("serverconnection@1394");
        this.client.sendLight("E_ERROR");
        Thread.sleep(3000L);
        this.client.close();
        return;
        } catch (InterruptedException ex1) {
        DebugServer.println("serverconnection@105");
        this.client.sendLight("E_ERROR");
        this.client.close();
        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
        return;
        }
        }
        String proofOfWorkSHA256String = HashFunctions.getProofOfWorkString(split[2]);
        int proof = HashFunctions.getProofOfWorkNonce(split[2]);
        
        if (proof!=1||!HashFunctions.sha1(split[1] + proof).equals(proofOfWorkSHA256String)) {
        try {
        DebugServer.println("serverconnection@1394");
        this.client.sendLight("E_ERROR");
        Thread.sleep(3000L);
        this.client.close();
        return;
        } catch (InterruptedException ex1) {
        DebugServer.println("serverconnection@105");
        this.client.sendLight("E_ERROR");
        this.client.close();
        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
        return;
        }
        }*/

        if (!pubKeys.contains(split[1])) {
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex1) {
                DebugServer.println("serverconnection@105");
                this.client.sendLight("E_ERROR");
                this.client.close();
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
                return;
            }
        }
        String hostAddress = client.getHostAddress();
        if (!pubKeysMap.containsKey(hostAddress)) {
            try {
                this.client.sendLight("E_ERROR");
                Thread.sleep(3000L);
                this.client.close();
                return;
            } catch (InterruptedException ex1) {
                DebugServer.println("serverconnection@105");
                this.client.sendLight("E_ERROR");
                this.client.close();
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
                return;
            }
        }
        if (ips.containsKey(hostAddress)) {
            String get = ips.get(hostAddress);
            if (syncedtrades.containsKey(get) && syncedtrades.containsKey(hostAddress)) {
                try {
                    this.client.sendLight("E_ERROR");
                    Thread.sleep(3000L);
                    this.client.close();
                    return;
                } catch (InterruptedException ex1) {
                    DebugServer.println("serverconnection@105");
                    this.client.sendLight("E_ERROR");
                    this.client.close();
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
                    return;
                }
            }
            ips.remove(hostAddress);
            ips.remove(get);
        }
        pubKeys.remove(split[1]);
        pubKeysMap.remove(hostAddress);
        pubKeysMap2.remove(split[1]);
        tradeAccountsIp.remove(hostAddress);
        tradeAccountsIp2.remove(hostAddress);
        pubkeysAddresses.remove(split[1]);
        this.client.sendLight("ALL_OK");
        this.client.close();
        return;
    }
}
