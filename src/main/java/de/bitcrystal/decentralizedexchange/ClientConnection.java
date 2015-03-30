/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import de.bitcrystal.decentralizedexchange.security.BitCrystalKeyGenerator;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.io.File;
import java.net.Socket;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ABC
 */
public class ClientConnection implements Runnable {

    private static Map<String, String> trader = new ConcurrentHashMap<String, String>();
    private static List<String> list = new CopyOnWriteArrayList<String>();
    private static PublicKey lastPubKey = null;
    private static String tradeAccount = "";
    private static String tradeAccount2 = "";
    private static String tradebtcry2btc = "";
    private static String tradebtc2btcry = "";
    private static String currentTradeAddress = "";
    private static String tradeWithAddress = "";
    private static boolean isSynced = false;
    private static boolean isStarted = false;
    private static boolean isEnded = false;
    private TCPClientSecurity server;
    private String command;
    private JSONObject clientJSON = null;

    public ClientConnection(TCPClient server, String command) {
        this.server = DecentralizedExchange.getSecurityClient(server);
        this.command = command;
        this.initClient();
    }

    public void run() {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (command == null || command.isEmpty()) {
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
                    if (split[0].equalsIgnoreCase("ADD")) {
                        if (!currentTradeAddress.isEmpty()) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        String newAddress = bitcoinrpc.getNewAddress();
                        currentTradeAddress = newAddress;
                        this.saveClient();
                        String pubKey = bitcoinrpc.getPubKey(newAddress);
                        String privKey = bitcoinrpc.getPrivKey(newAddress);
                        bitcrystalrpc.importPrivKey(privKey);
                        System.out.println(privKey);
                        System.out.println(newAddress);
                        this.server.send("add," + pubKey);
                        String recv = this.server.recv();
                        System.out.println(recv);
                        System.out.println("cool");
                        this.server.close();
                    }

                    if (split[0].equalsIgnoreCase("TRADEABORT")) {
                        tradeAccount = "";
                        tradebtc2btcry = "";
                        tradebtcry2btc = "";
                        this.server.send("CANCEL_ALL");
                        this.server.recv();
                        this.server.close();
                        return;
                    }

                    if (split[0].equalsIgnoreCase("SYNCTRADE")) {
                        if (isSynced) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        if (tradebtc2btcry.isEmpty() && tradebtcry2btc.isEmpty()) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        if (!tradebtc2btcry.isEmpty() && !tradebtcry2btc.isEmpty()) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        if (!tradebtc2btcry.isEmpty()) {
                            this.server.send("synctrade;btc2btcry,," + tradebtc2btcry);
                            String recv = this.server.recv();
                            if (recv.equals("E_ERROR")) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            this.server.send("AWESOME!");
                            this.server.close();
                            isSynced = true;
                            return;
                        }

                        if (!tradebtcry2btc.isEmpty()) {
                            this.server.send("synctrade;btcry2btc,," + tradebtcry2btc);
                            String recv = this.server.recv();
                            if (recv.equals("E_ERROR")) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            this.server.send("AWESOME!");
                            this.server.close();
                            isSynced = true;
                            return;
                        }
                    }

                    if (split[0].equalsIgnoreCase("STARTTRADE")) {
                        if (!isSynced) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        this.server.send("starttrade");
                        String recv = this.server.recv();
                        if (recv.equals("E_ERROR")) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        this.server.send(":)");
                        this.server.close();
                        isStarted = true;
                    }

                    if (split[0].equalsIgnoreCase("ENDTRADEME")) {
                        if (!isStarted) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        this.server.send("endtrademe;" + tradeAccount);
                        String recv = this.server.recv();
                        if (recv.equals("E_ERROR")) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        if (!tradebtc2btcry.isEmpty()) {
                            if (!recv.startsWith("btc2btcry;;")) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            if (!recv.contains(";;")) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String[] split2 = recv.split(";;");
                            if (split2.length != 5) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String myTradeAccount = split2[1];
                            String myTradeWithAddress = split2[2];
                            String myPrice = split2[3];
                            String myTransaction = split2[4];
                            if (!myTradeAccount.equals(tradeAccount)) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            if (!myTradeWithAddress.equals(tradeWithAddress)) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            double price = 0;
                            try {
                                price = Double.parseDouble(myPrice);
                                if (price <= 0) {
                                    throw new Exception();
                                }
                            } catch (Exception ex) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String[] split1 = tradebtc2btcry.split(";;");
                            double thisprice = 0;
                            try {
                                thisprice = Double.parseDouble(split1[1]);
                                if (thisprice <= 0 || price != thisprice) {
                                    throw new Exception();
                                }
                            } catch (Exception ex) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            Object[] values = {tradeAccount, tradeWithAddress, price};
                            String createrawtransaction_multisig = bitcoinrpc.createrawtransaction_multisig(values);
                            if (!createrawtransaction_multisig.equals(myTransaction)) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            JsonObject decodeRawTransactionMultisig = null;
                            try {
                                decodeRawTransactionMultisig = bitcoinrpc.decodeRawTransactionMultisig(createrawtransaction_multisig);
                            } catch (Exception ex2) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String signrawtransaction_multisig = bitcoinrpc.signrawtransaction_multisig(createrawtransaction_multisig);
                            this.server.send(signrawtransaction_multisig);
                            String recv1 = this.server.recv();
                            if (recv1.equals("E_ERROR")) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            if (recv1.equals("ALL_OK")) {
                                this.server.close();
                                return;
                            }
                        } else if (!tradebtcry2btc.isEmpty()) {
                            if (!recv.startsWith("btcry2btc;;")) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            if (!recv.contains(";;")) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String[] split2 = recv.split(";;");
                            if (split2.length != 5) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String myTradeAccount = split2[1];
                            String myTradeWithAddress = split2[2];
                            String myPrice = split2[3];
                            String myTransaction = split2[4];
                            if (!myTradeAccount.equals(tradeAccount)) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            if (!myTradeWithAddress.equals(tradeWithAddress)) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            double price = 0;
                            try {
                                price = Double.parseDouble(myPrice);
                                if (price <= 0) {
                                    throw new Exception();
                                }
                            } catch (Exception ex) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String[] split1 = tradebtc2btcry.split(";;");
                            double thisprice = 0;
                            try {
                                thisprice = Double.parseDouble(split1[1]);
                                if (thisprice <= 0 || price != thisprice) {
                                    throw new Exception();
                                }
                            } catch (Exception ex) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            Object[] values = {tradeAccount, tradeWithAddress, price};
                            String createrawtransaction_multisig = bitcrystalrpc.createrawtransaction_multisig(values);
                            if (!createrawtransaction_multisig.equals(myTransaction)) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            JsonObject decodeRawTransactionMultisig = null;
                            try {
                                decodeRawTransactionMultisig = bitcrystalrpc.decodeRawTransactionMultisig(createrawtransaction_multisig);
                            } catch (Exception ex2) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String signrawtransaction_multisig = bitcrystalrpc.signrawtransaction_multisig(createrawtransaction_multisig);
                            this.server.send(signrawtransaction_multisig);
                            String recv1 = this.server.recv();
                            if (recv1.equals("E_ERROR")) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            if (recv1.equals("ALL_OK")) {
                                this.server.close();
                                return;
                            }
                        }
                    }

                    if (split[0].equalsIgnoreCase("ENDTRADEOTHER")) {
                        if (!isStarted) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        this.server.send("endtradeother;" + tradeAccount);
                        String recv = this.server.recv();
                        if (recv.equals("E_ERROR")) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        if (!tradebtc2btcry.isEmpty()) {
                            String recv1 = this.server.recv();
                            String[] split1 = tradebtc2btcry.split(";;");
                            JsonObject decodeRawTransactionMultisig1 = null;
                            try {
                                decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(recv1);
                                if (!decodeRawTransactionMultisig1.has("complete")) {
                                    this.server.send("E_ERROR");
                                    this.server.close();
                                    return;
                                }
                                String asString = decodeRawTransactionMultisig1.get("toaddress").getAsString();
                                double asDouble = decodeRawTransactionMultisig1.get("amount").getAsDouble();
                                String currencyprefix = decodeRawTransactionMultisig1.get("currencyprefix").getAsString();
                                if (!(("" + asDouble).equals(split1[0])) || !currencyprefix.equals("BTCRY") || !asString.equals(currentTradeAddress)) {
                                    this.server.send("E_ERROR");
                                    this.server.close();
                                    return;
                                }
                            } catch (Exception ex2) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String signrawtransaction_multisig1 = bitcrystalrpc.signrawtransaction_multisig(recv1);
                            try {
                                decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                                JsonElement get = decodeRawTransactionMultisig1.get("complete");
                                if (get.getAsBoolean() == true) {
                                    throw new Exception();
                                }
                            } catch (Exception ex2) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            this.server.send(signrawtransaction_multisig1);
                            this.server.recv();
                            this.server.close();
                        } else if (!tradebtcry2btc.isEmpty()) {
                            String recv1 = this.server.recv();
                            String[] split1 = tradebtcry2btc.split(";;");
                            JsonObject decodeRawTransactionMultisig1 = null;
                            try {
                                decodeRawTransactionMultisig1 = bitcoinrpc.decodeRawTransactionMultisig(recv1);
                                if (!decodeRawTransactionMultisig1.has("complete")) {
                                    this.server.send("E_ERROR");
                                    this.server.close();
                                    return;
                                }
                                String asString = decodeRawTransactionMultisig1.get("toaddress").getAsString();
                                double asDouble = decodeRawTransactionMultisig1.get("amount").getAsDouble();
                                String currencyprefix = decodeRawTransactionMultisig1.get("currencyprefix").getAsString();
                                if (!(("" + asDouble).equals(split1[0])) || !currencyprefix.equals("BTC") || !asString.equals(currentTradeAddress)) {
                                    this.server.send("E_ERROR");
                                    this.server.close();
                                    return;
                                }
                            } catch (Exception ex2) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            String signrawtransaction_multisig1 = bitcoinrpc.signrawtransaction_multisig(recv1);
                            try {
                                decodeRawTransactionMultisig1 = bitcoinrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                                JsonElement get = decodeRawTransactionMultisig1.get("complete");
                                if (get.getAsBoolean() == true) {
                                    throw new Exception();
                                }
                            } catch (Exception ex2) {
                                this.server.send("E_ERROR");
                                this.server.close();
                                return;
                            }
                            this.server.send(signrawtransaction_multisig1);
                            this.server.recv();
                            this.server.close();
                        }
                    }
                }
                break;

                case 2: {
                    if (split[0].equalsIgnoreCase("TRADEWITH")) {
                        if (!tradeAccount.isEmpty() && !tradeAccount2.isEmpty()) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        this.server.send("tradewith," + split[1]);
                        String recv = this.server.recv();
                        if (recv.equals("E_ERROR")) {
                            this.server.close();
                            return;
                        }
                        if (!recv.contains(",,")) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }

                        String[] split1 = recv.split(",,");
                        if (split1.length != 3) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }

                        tradeWithAddress = split[2];
                        tradeAccount = currentTradeAddress + "," + tradeWithAddress;
                        tradeAccount2 = tradeWithAddress + "," + currentTradeAddress;
                        Object[] values1 = {split1[0], tradeAccount};
                        Object[] values2 = {split1[1], tradeAccount};
                        Object[] values3 = {split1[0], tradeAccount2};
                        Object[] values4 = {split1[1], tradeAccount2};
                        if (!bitcoinrpc.accountexists(tradeAccount)) {
                            bitcoinrpc.addmultisigaddressex(values1);
                            bitcrystalrpc.addmultisigaddressex(values2);
                        }

                        if (!bitcoinrpc.accountexists(tradeAccount2)) {
                            bitcoinrpc.addmultisigaddressex(values3);
                            bitcrystalrpc.addmultisigaddressex(values4);
                        }
                        this.server.send("ALL_OK");
                        this.server.close();
                    }
                }
                break;

                case 3: {
                    if (split[0].equalsIgnoreCase("CREATETRADEBTCRY2BTC")) {
                        tradebtc2btcry = "";
                        if (tradeAccount.isEmpty() || tradeAccount2.isEmpty()) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        if (!tradebtcry2btc.isEmpty()) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        this.server.send("E_ERROR");
                        this.server.close();
                        double amount = 0;
                        double price = 0;
                        double balance = bitcoinrpc.getBalance(tradeAccount);
                        try {
                            amount = Double.parseDouble(split[1]);
                            price = Double.parseDouble(split[2]);
                            if (amount <= 0 || price <= 0 || price < balance) {
                                throw new Exception();
                            }
                        } catch (Exception ex) {
                            tradebtcry2btc = "";
                            tradebtc2btcry = "";
                            return;
                        }

                        tradebtcry2btc = amount + ",," + price + ",," + tradeAccount + ",," + tradeAccount2;
                        tradebtc2btcry = "";
                    }

                    if (split[0].equalsIgnoreCase("CREATETRADEBTC2BTCRY")) {
                        tradebtcry2btc = "";
                        if (tradeAccount.isEmpty() || tradeAccount2.isEmpty()) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        if (!tradebtc2btcry.isEmpty()) {
                            this.server.send("E_ERROR");
                            this.server.close();
                            return;
                        }
                        this.server.send("E_ERROR");
                        this.server.close();
                        double amount = 0;
                        double price = 0;
                        double balance = bitcrystalrpc.getBalance(tradeAccount);
                        try {
                            amount = Double.parseDouble(split[1]);
                            price = Double.parseDouble(split[2]);
                            if (amount <= 0 || price <= 0 || price < balance) {
                                throw new Exception();
                            }
                        } catch (Exception ex) {
                            tradebtcry2btc = "";
                            tradebtc2btcry = "";
                            return;
                        }
                        tradebtc2btcry = amount + ",," + price + ",," + tradeAccount + ",," + tradeAccount2;
                        tradebtcry2btc = "";
                    }
                }
                break;
            }
        } catch (Exception ex) {
            this.server.send("E_ERROR");
            this.server.close();
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }
    
    private synchronized void initClient() {
        System.out.println("test");
        clientJSON = this.server.loadJSONObject("client", "", "client.properties");
        System.out.println("cool");
        if (clientJSON == null) {
            try {
                JSONObject json = new JSONObject();
                json.put("currentTradeAddress", "");
                json.put("isEnded", "");
                json.put("isStarted", "");
                json.put("isSynced", "");
                json.put("tradeAccount", "");
                json.put("tradeAccount2", "");
                json.put("tradeWithAddress", "");
                json.put("tradebtc2btcry", "");
                json.put("tradebtcry2ntc", "");
                System.out.println("nein");
                this.server.saveJSONObject(json, "client", "", "client.properties");
                System.out.println("hier");
                clientJSON = json;
            } catch (JSONException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                currentTradeAddress = clientJSON.getString("currentTradeAddress");
            } catch (JSONException ex) {
                currentTradeAddress = "";
            }
            try {
                tradeWithAddress = clientJSON.getString("tradeWithAddress");
            } catch (JSONException ex) {
                tradeWithAddress = "";
            }
            try {
                tradeAccount = clientJSON.getString("tradeAccount");
            } catch (JSONException ex) {
                tradeAccount = "";
            }
            try {
                tradeAccount2 = clientJSON.getString("tradeAccount2");
            } catch (JSONException ex) {
                tradeAccount2 = "";
            }
            try {
                tradebtc2btcry = clientJSON.getString("tradebtc2btcry");
            } catch (JSONException ex) {
                tradebtc2btcry = "";
            }
            try {
                tradebtcry2btc = clientJSON.getString("tradebtcry2btc");
            } catch (JSONException ex) {
                tradebtcry2btc = "";
            }
            try {
                isEnded = clientJSON.getBoolean("isEnded");
            } catch (JSONException ex) {
                isEnded = false;
            }
            try {
                isSynced = clientJSON.getBoolean("isSynced");
            } catch (JSONException ex) {
                isSynced = false;
            }
            try {
                isStarted = clientJSON.getBoolean("isStarted");
            } catch (JSONException ex) {
                isStarted = false;
            }
        }
    }

    private synchronized void saveClient() {
        JSONObject json = this.server.loadJSONObject("client", "", "client.properties");
        if (json == null) {
            return;
        }
        try {
            json.put("currentTradeAddress", currentTradeAddress);
            json.put("isEnded", isEnded);
            json.put("isStarted", isStarted);
            json.put("isSynced", isSynced);
            json.put("tradeAccount", tradeAccount);
            json.put("tradeAccount2", tradeAccount2);
            json.put("tradeWithAddress", tradeWithAddress);
            json.put("tradebtc2btcry", tradebtc2btcry);
            json.put("tradebtcry2ntc", tradebtcry2btc);
            this.server.saveJSONObject(json, "client", "", "client.properties");
            clientJSON = json;
        } catch (JSONException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
