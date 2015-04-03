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
            System.out.println("mausge");
            if (command == null || command.isEmpty()) {
                this.server.send("E_ERROR");
                this.server.close();
                System.out.println("mausssssssge");
                return;
            }
            if (!command.contains(" ")) {
                command = command + " ";
            }
            String[] split = command.split(" ");
            System.out.println(split.length);
            System.out.println(split[0]);
            if (split.length < 1) {
                this.server.send("E_ERROR");
                this.server.close();
                System.out.println("mausge");
                System.out.println("mauasdadaaddasge");
                return;
            }
            switch (split.length) {
                case 1: {
                    if (split[0].equalsIgnoreCase("ADD")) {
                        System.out.println("add open");
                        add();
                        System.out.println("add close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("GETCURRENTTRADERADDRESS")) {
                        System.out.println(currentTradeAddress);
                        this.server.close();
                        return;
                    }

                    if (split[0].equalsIgnoreCase("GETCURRENTTRADEWITHADDRESS")) {
                        System.out.println(tradeWithAddress);
                        this.server.close();
                        return;
                    }

                    if (split[0].equalsIgnoreCase("GETCURRENTTRADE")) {
                        if (!tradebtc2btcry.isEmpty()) {
                            System.out.println(tradebtc2btcry);
                        } else if (!tradebtc2btcry.isEmpty()) {
                            System.out.println(tradebtcry2btc);
                        } else {
                            System.out.println("No Trade available!");
                        }
                        this.server.close();
                        return;
                    }

                    if (split[0].equalsIgnoreCase("TRADEABORT")) {
                        System.out.println("tradeabort open");
                        tradeabort();
                        System.out.println("tradeabort close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("SYNCTRADE")) {
                        System.out.println("synctrade open");
                        synctrade();
                        System.out.println("synctrade close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("STARTTRADE")) {
                        System.out.println("starttrade open");
                        starttrade();
                        System.out.println("starttrade close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("ENDTRADEME")) {
                        System.out.println("endtrademe open");
                        endtrademe();
                        System.out.println("endtrademe close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("ENDTRADEOTHER")) {
                        System.out.println("endtradeother open");
                        endtradeother();
                        System.out.println("endtradeother close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("ENDTRADE")) {
                        System.out.println("endtrade open");
                        endtrade();
                        System.out.println("endtrade close");
                        return;
                    }
                }
                break;

                case 2: {
                    if (split[0].equalsIgnoreCase("TRADEWITH")) {
                        System.out.println("tradewith open");
                        tradewith(split);
                        System.out.println("tradewith close");
                        return;
                    }
                }
                break;

                case 3: {
                    if (split[0].equalsIgnoreCase("CREATETRADEBTCRY2BTC")) {
                        System.out.println("createbtcry2btc open");
                        createtradebtcry2btc(split);
                        System.out.println("createbtcry2btc close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("CREATETRADEBTC2BTCRY")) {
                        System.out.println("createtradebtc2ntcry open");
                        createtradebtc2btcry(split);
                        System.out.println("createtradebtc2btcry close");
                        return;
                    }
                }
                break;
            }
        } catch (Exception ex) {
            System.out.println("clientconnection@627");
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
                System.out.println("coolvvv");
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
                System.out.println("coolffff");
                currentTradeAddress = clientJSON.getString("currentTradeAddress");
            } catch (JSONException ex) {
                System.out.println("coolddddd");
                currentTradeAddress = "";
            }
            try {
                tradeWithAddress = clientJSON.getString("tradeWithAddress");
                System.out.println("cxxxxxxxxool");
            } catch (JSONException ex) {
                tradeWithAddress = "";
                System.out.println("ssssssssssscool");
            }
            try {
                tradeAccount = clientJSON.getString("tradeAccount");
                System.out.println("xxadadaadcool");
            } catch (JSONException ex) {
                tradeAccount = "";
                System.out.println("ddasfasdgcool");
            }
            try {
                tradeAccount2 = clientJSON.getString("tradeAccount2");
                System.out.println("cxyxcaysacxycool");
            } catch (JSONException ex) {
                tradeAccount2 = "";
                System.out.println("cofdgfwe43eewol");
            }
            try {
                tradebtc2btcry = clientJSON.getString("tradebtc2btcry");
                System.out.println("cossafdasfdcdsdfol");
            } catch (JSONException ex) {
                tradebtc2btcry = "";
                System.out.println("weqeqqeqcool");
            }
            try {
                tradebtcry2btc = clientJSON.getString("tradebtcry2btc");
                System.out.println("cdycycycyool");
            } catch (JSONException ex) {
                tradebtcry2btc = "";
                System.out.println("cooaddadasdasl");
            }
            try {
                isEnded = clientJSON.getBoolean("isEnded");
                System.out.println("coadadadadaol");
            } catch (JSONException ex) {
                isEnded = false;
                System.out.println("coasdadadol");
            }
            try {
                isSynced = clientJSON.getBoolean("isSynced");
                System.out.println("codadadaadadadadadol");
            } catch (JSONException ex) {
                isSynced = false;
                System.out.println("daaadadadadadadaddcool");
            }
            try {
                isStarted = clientJSON.getBoolean("isStarted");
                System.out.println("caddaadadadool");
            } catch (JSONException ex) {
                isStarted = false;
                System.out.println("cossssssol");
            }
        }
    }

    private synchronized void saveClient() {
        System.out.println("clientconnection@727");
        JSONObject json = this.server.loadJSONObject("client", "", "client.properties");
        if (json == null) {
            return;
        }
        try {
            System.out.println("clientconnection@733");
            json.put("currentTradeAddress", currentTradeAddress);
            json.put("isEnded", isEnded);
            json.put("isStarted", isStarted);
            json.put("isSynced", isSynced);
            json.put("tradeAccount", tradeAccount);
            json.put("tradeAccount2", tradeAccount2);
            json.put("tradeWithAddress", tradeWithAddress);
            json.put("tradebtc2btcry", tradebtc2btcry);
            json.put("tradebtcry2btc", tradebtcry2btc);
            this.server.saveJSONObject(json, "client", "", "client.properties");
            clientJSON = json;
            System.out.println("clientconnection@745");
        } catch (JSONException ex) {
            System.out.println("clientconnection@747");
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("end");
    }

    private void add() {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            System.out.println("clientconnection@79");
            if (!currentTradeAddress.isEmpty()) {
                this.server.send("E_ERROR");
                this.server.close();
                System.out.println("mausasdadadadae");
                return;
            }
            System.out.println("clientconnection@85");
            System.out.println("maadadadadadadadadusge");
            String newAddress = bitcoinrpc.getNewAddress();
            currentTradeAddress = newAddress;
            System.out.println("mauadadadadadsge");
            System.out.println("clientconnection@91");
            this.saveClient();
            System.out.println("madaadadadaadaadadadusge");
            String pubKey = bitcoinrpc.getPubKey(newAddress);
            String privKey = bitcoinrpc.getPrivKey(newAddress);
            if (!bitcrystalrpc.addressexists(newAddress)) {
                bitcrystalrpc.importPrivKey(privKey);
            }
            System.out.println(privKey);
            System.out.println(newAddress);
            this.server.send("add," + pubKey);
            System.out.println("mausssdadsasdasdasdge");
            String recv = this.server.recv();
            System.out.println("mausasadsasdssge");
            System.out.println(recv);
            System.out.println("cool");
            this.server.close();
            this.saveClient();
            return;
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void synctrade() {
        if (isSynced) {
            System.out.println("clientconnection@123");
            this.server.send("E_ERROR");
            this.server.close();
            return;
        }
        if (tradebtc2btcry.isEmpty() && tradebtcry2btc.isEmpty()) {
            System.out.println("clientconnection@129");
            this.server.send("E_ERROR");
            this.server.close();
            return;
        }
        if (!tradebtc2btcry.isEmpty() && !tradebtcry2btc.isEmpty()) {
            System.out.println("clientconnection@135");
            this.server.send("E_ERROR");
            this.server.close();
            return;
        }
        if (!tradebtc2btcry.isEmpty()) {
            System.out.println("clientconnection@141");
            this.server.send("synctrade;btc2btcry,," + tradebtc2btcry);
            String recv = this.server.recv();
            if (recv.equals("E_ERROR")) {
                System.out.println("clientconnection@145");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            this.server.send("AWESOME!");
            this.server.close();
            isSynced = true;
            this.saveClient();
            return;
        }

        if (!tradebtcry2btc.isEmpty()) {
            System.out.println("clientconnection@157");
            this.server.send("synctrade;btcry2btc,," + tradebtcry2btc);
            String recv = this.server.recv();
            System.out.println(recv);
            if (recv.equals("E_ERROR")) {
                System.out.println("clientconnection@162");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            this.server.send("AWESOME!");
            this.server.close();
            isSynced = true;
            this.saveClient();
            return;
        }
    }

    private void endtrade() {
        this.server.send("endtrade");
        String recv = this.server.recv();
        if (recv.equals("E_ERROR")) {
            this.server.close();
            return;
        }
        if (recv.equals("ALL_OK")) {
            System.out.println("YEAH I'VE WON!!!!!!!!!!!!!!!!!!!11");
            this.server.close();
            return;
        }
    }

    private void tradeabort() {
        tradeAccount = "";
        tradebtc2btcry = "";
        tradebtcry2btc = "";
        this.server.send("CANCEL_ALL");
        this.server.recv();
        this.server.close();
        this.saveClient();
        return;
    }

    private void starttrade() {
        System.out.println("clientconnection@175");
        if (!isSynced) {
            System.out.println("clientconnection@177");
            this.server.send("E_ERROR");
            this.server.close();
            return;
        }
        System.out.println("clientconnection@182");
        this.server.send("starttrade");
        System.out.println("clientconnection@184");
        String recv = this.server.recv();
        System.out.println(recv);
        System.out.println("clientconnection@187");
        if (recv.equals("E_ERROR")) {
            System.out.println("clientconnection@185");
            this.server.send("E_ERROR");
            this.server.close();
            return;
        }
        System.out.println("clientconnection@194");
        this.server.send(":)");
        this.server.close();
        isStarted = true;
        this.saveClient();
        return;
    }

    private void endtrademe() {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            System.out.println("clientconnection@201");
            if (!isStarted) {
                System.out.println("clientconnection@203");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            System.out.println("clientconnection@208");
            this.server.send("endtrademe;" + tradeAccount);
            System.out.println("clientconnection@210");
            String recv = this.server.recv();
            System.out.println(recv);
            System.out.println("clientconnection@213");
            if (recv.equals("E_ERROR")) {
                System.out.println("clientconnection@215");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            if (!tradebtc2btcry.isEmpty()) {
                System.out.println("clientconnection@221");
                if (!recv.startsWith("btc2btcry;;")) {
                    System.out.println("clientconnection@223");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                System.out.println("clientconnection@228");
                if (!recv.contains(";;")) {
                    System.out.println("clientconnection@229");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                String[] split2 = recv.split(";;");
                System.out.println("clientconnection@236");
                if (split2.length != 5) {
                    System.out.println("clientconnection@238");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                String myTradeAccount = split2[1];
                String myTradeWithAddress = split2[2];
                String myPrice = split2[3];
                String myTransaction = split2[4];
                if (!myTradeAccount.equals(tradeAccount)) {
                    System.out.println("clientconnection@248");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                if (!myTradeWithAddress.equals(tradeWithAddress)) {
                    System.out.println("clientconnection@254");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                double price = 0;
                try {
                    price = Double.parseDouble(myPrice);
                    if (price <= 0) {
                        System.out.println("clientconnection@263");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    System.out.println("clientconnection@267");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                System.out.println(tradebtc2btcry);
                String[] split1 = tradebtc2btcry.split(",,");
                System.out.println(split1[0]);
                System.out.println(split1[1]);
                double thisprice = 0;
                try {
                    System.out.println("clientconnection@275");
                    System.out.println(split1[1]);
                    thisprice = Double.parseDouble(split1[1]);
                    if (thisprice <= 0 || price != thisprice) {
                        System.out.println("clientconnection@278");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    System.out.println("clientconnection@282");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                Object[] values = {tradeAccount, tradeWithAddress, price, 0.00, 0};
                String createrawtransaction_multisig = bitcoinrpc.createrawtransaction_multisig(values);
                System.out.println(myTransaction);
                System.out.println(createrawtransaction_multisig);
                if (!bitcoinrpc.testtransactionequals_multisig(createrawtransaction_multisig, myTransaction)) {
                    System.out.println("clientconnection@290");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                System.out.println("clientconnection@295");
                JsonObject decodeRawTransactionMultisig = null;
                try {
                    System.out.println("clientconnection@297");
                    decodeRawTransactionMultisig = bitcoinrpc.decodeRawTransactionMultisig(createrawtransaction_multisig);
                } catch (Exception ex2) {
                    System.out.println("clientconnection@301");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                String signrawtransaction_multisig = bitcoinrpc.signrawtransaction_multisig(createrawtransaction_multisig);
                this.server.send(signrawtransaction_multisig);
                String recv1 = this.server.recv();
                if (recv1.equals("E_ERROR")) {
                    System.out.println("clientconnection@310");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                if (recv1.equals("ALL_OK")) {
                    System.out.println("clientconnection@316");
                    this.server.close();
                    return;
                }
            } else if (!tradebtcry2btc.isEmpty()) {
                System.out.println("clientconnection@321");
                if (!recv.startsWith("btcry2btc;;")) {
                    System.out.println("clientconnection@323");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                if (!recv.contains(";;")) {
                    System.out.println("clientconnection@329");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                String[] split2 = recv.split(";;");
                System.out.println("clientconnection@335");
                if (split2.length != 5) {
                    System.out.println("clientconnection@337");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                String myTradeAccount = split2[1];
                String myTradeWithAddress = split2[2];
                String myPrice = split2[3];
                String myTransaction = split2[4];
                if (!myTradeAccount.equals(tradeAccount)) {
                    System.out.println("clientconnection@347");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                if (!myTradeWithAddress.equals(tradeWithAddress)) {
                    System.out.println("clientconnection@353");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                double price = 0;
                try {
                    System.out.println("clientconnection@360");
                    price = Double.parseDouble(myPrice);
                    if (price <= 0) {
                        System.out.println("clientconnection@363");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    System.out.println("clientconnection@367");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                String[] split1 = tradebtcry2btc.split(",,");
                double thisprice = 0;
                try {
                    System.out.println("clientconnection@375");
                    System.out.println(split1[1]);
                    thisprice = Double.parseDouble(split1[1]);
                    if (thisprice <= 0 || price != thisprice) {
                        System.out.println("clientconnection@378");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    System.out.println("clientconnection@381");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                Object[] values = {tradeAccount, tradeWithAddress, price, 0.00, 0};
                String createrawtransaction_multisig = bitcrystalrpc.createrawtransaction_multisig(values);
               if (!bitcrystalrpc.testtransactionequals_multisig(createrawtransaction_multisig, myTransaction)) {
                    System.out.println("clientconnection@390");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                JsonObject decodeRawTransactionMultisig = null;
                try {
                    System.out.println("clientconnection@397");
                    decodeRawTransactionMultisig = bitcrystalrpc.decodeRawTransactionMultisig(createrawtransaction_multisig);
                } catch (Exception ex2) {
                    System.out.println("clientconnection@400");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                String signrawtransaction_multisig = bitcrystalrpc.signrawtransaction_multisig(createrawtransaction_multisig);
                this.server.send(signrawtransaction_multisig);
                String recv1 = this.server.recv();
                System.out.println("clientconnection@408");
                if (recv1.equals("E_ERROR")) {
                    System.out.println("clientconnection@410");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                if (recv1.equals("ALL_OK")) {
                    System.out.println("clientconnection@416");
                    this.server.close();
                    this.saveClient();
                    return;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void endtradeother() {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            System.out.println("clientconnection@424");
            if (!isStarted) {
                System.out.println("clientconnection@362");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            System.out.println("clientconnection@367");
            this.server.send("endtradeother;" + tradeAccount);
            System.out.println("clientconnection@369");
            String recv = this.server.recv();
            System.out.println(recv);
            System.out.println("clientconnection@370");
            if (recv.equals("E_ERROR")) {
                System.out.println("clientconnection@374");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            if (!tradebtc2btcry.isEmpty()) {
                System.out.println("clientconnection@380");
                String[] split1 = tradebtc2btcry.split(",,");
                JsonObject decodeRawTransactionMultisig1 = null;
                try {
                    System.out.println("clientconnection@385");
                    decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(recv);
                    if (!decodeRawTransactionMultisig1.has("complete")) {
                        System.out.println("clientconnection@389");
                        this.server.send("E_ERROR");
                        this.server.close();
                        return;
                    }
                    String asString = decodeRawTransactionMultisig1.get("toaddress").getAsString();
                    double asDouble = decodeRawTransactionMultisig1.get("amount").getAsDouble();
                    String currencyprefix = decodeRawTransactionMultisig1.get("currencyprefix").getAsString();
                    if (!(("" + asDouble).equals(split1[0]))
                            || //!currencyprefix.equals("BTCRY") ||
                            !asString.equals(currentTradeAddress)) {
                        System.out.println("clientconnection@398");
                        this.server.send("E_ERROR");
                        this.server.close();
                        return;
                    }
                } catch (Exception ex2) {
                    System.out.println("clientconnection@404");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                String signrawtransaction_multisig1 = bitcrystalrpc.signrawtransaction_multisig(recv);
                try {
                    System.out.println("clientconnection@411");
                    decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                    JsonElement get = decodeRawTransactionMultisig1.get("complete");
                    if (get.getAsBoolean() == true) {
                        System.out.println("clientconnection@415");
                        throw new Exception();
                    }
                } catch (Exception ex2) {
                    System.out.println("clientconnection@419");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                System.out.println("clientconnection@424");
                this.server.send(signrawtransaction_multisig1);
                System.out.println("clientconnection@426");
                this.server.recv();
                this.server.close();
            } else if (!tradebtcry2btc.isEmpty()) {
                System.out.println("clientconnection@430");
                System.out.println(recv);
                System.out.println("clientconnection@432");
                String[] split1 = tradebtcry2btc.split(",,");
                JsonObject decodeRawTransactionMultisig1 = null;
                try {
                    System.out.println("clientconnection@437");
                    decodeRawTransactionMultisig1 = bitcoinrpc.decodeRawTransactionMultisig(recv);
                    if (!decodeRawTransactionMultisig1.has("complete")) {
                        System.out.println("clientconnection@440");
                        this.server.send("E_ERROR");
                        this.server.close();
                        return;
                    }
                    String asString = decodeRawTransactionMultisig1.get("toaddress").getAsString();
                    double asDouble = decodeRawTransactionMultisig1.get("amount").getAsDouble();
                    String currencyprefix = decodeRawTransactionMultisig1.get("currencyprefix").getAsString();
                    if (!(("" + asDouble).equals(split1[0]))
                            //|| !currencyprefix.equals("BTC") 
                            || !asString.equals(currentTradeAddress)) {
                        System.out.println("clientconnection@449");
                        this.server.send("E_ERROR");
                        this.server.close();
                        return;
                    }
                } catch (Exception ex2) {
                    System.out.println("clientconnection@455");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                System.out.println("clientconnection@460");
                String signrawtransaction_multisig1 = bitcoinrpc.signrawtransaction_multisig(recv);
                try {
                    System.out.println("clientconnection@462");
                    decodeRawTransactionMultisig1 = bitcoinrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                    JsonElement get = decodeRawTransactionMultisig1.get("complete");
                    if (get.getAsBoolean() == true) {
                        System.out.println("clientconnection@466");
                        throw new Exception();
                    }
                } catch (Exception ex2) {
                    System.out.println("clientconnection@470");
                    this.server.send("E_ERROR");
                    this.server.close();
                    return;
                }
                System.out.println("clientconnection@476");
                this.server.send(signrawtransaction_multisig1);
                System.out.println("clientconnection@478");
                this.server.recv();
                System.out.println("clientconnection@480");
                this.server.close();
                this.saveClient();
                return;
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tradewith(String[] split) {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            System.out.println("sssssad");
            if (!tradeAccount.isEmpty() && !tradeAccount2.isEmpty()) {
                this.server.send("E_ERROR");
                this.server.close();
                System.out.println("ssasdas");
                return;
            }
            System.out.println("asljkfjcyklnjkcbgzbdsyxjbvcbcb");
            this.server.send("tradewith," + split[1]);
            String recv = this.server.recv();
            this.server.close();
            System.out.println(recv);
            System.out.println("ddddff");
            if (recv.equals("E_ERROR")) {
                this.server.close();
                System.out.println("asdasksdjkjasdkasjskdjsd");
                return;
            }
            if (!recv.contains(",,")) {
                this.server.close();
                System.out.println("xdadjkjsklhdjlfhjaskbhfjskdhjkdsfh");
                return;
            }

            String[] split1 = recv.split(",,");
            if (split1.length != 3) {
                this.server.close();
                System.out.println("skjsfkljdskljfdkljdsikljsdfkljdkl");
                return;
            }

            tradeWithAddress = split1[2];
            tradeAccount = currentTradeAddress + "," + tradeWithAddress;
            tradeAccount2 = tradeWithAddress + "," + currentTradeAddress;
            Object[] values1 = {split1[0], tradeAccount};
            Object[] values2 = {split1[1], tradeAccount};
            Object[] values3 = {split1[0], tradeAccount2};
            Object[] values4 = {split1[1], tradeAccount2};
            try {
                System.out.println("dddddd");
                bitcoinrpc.addmultisigaddressex(values1);
                bitcrystalrpc.addmultisigaddressex(values2);
                bitcoinrpc.addmultisigaddressex(values3);
                bitcrystalrpc.addmultisigaddressex(values4);
            } catch (Exception ex2) {
            }
            /*
            try {
            if (!bitcoinrpc.accountexists(tradeAccount)) {
            System.out.println("skfjkdjkldjkldnx,mvnc");
            bitcoinrpc.addmultisigaddressex(values1);
            bitcrystalrpc.addmultisigaddressex(values2);
            }
            
            if (!bitcoinrpc.accountexists(tradeAccount2)) {
            System.out.println("skjflkxadnklmxcvnbgds");
            bitcoinrpc.addmultisigaddressex(values3);
            bitcrystalrpc.addmultisigaddressex(values4);
            }
            } catch (Exception ex) {
            try {
            bitcoinrpc.addmultisigaddressex(values1);
            bitcrystalrpc.addmultisigaddressex(values2);
            } catch (Exception ex2) {
            }
            }*/
            System.out.println("djdnvncncnnccc");
            this.server.close();
            this.saveClient();
            return;
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createtradebtcry2btc(String[] split) {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            System.out.println("clientconnection@545");
            tradebtc2btcry = "";
            if (tradeAccount.isEmpty() || tradeAccount2.isEmpty()) {
                System.out.println("clientconnection@548");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            if (!tradebtcry2btc.isEmpty()) {
                System.out.println("clientconnection@553");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            System.out.println("clientconnection@559");
            this.server.send("E_ERROR");
            this.server.close();
            double amount = 0;
            double price = 0;
            double balance = bitcrystalrpc.getBalance(tradeAccount);
            try {
                System.out.println("clientconnection@566");
                amount = Double.parseDouble(split[1]);
                price = Double.parseDouble(split[2]);
                if (amount <= 0 || price <= 0 || price > balance) {
                    System.out.println("clientconnection@570");
                    throw new Exception();
                }
            } catch (Exception ex) {
                System.out.println("clientconnection@574");
                tradebtcry2btc = "";
                tradebtc2btcry = "";
                return;
            }

            tradebtcry2btc = amount + ",," + price + ",," + tradeAccount + ",," + tradeAccount2;
            tradebtc2btcry = "";
            this.saveClient();
            System.out.println("clientconnection@582");
            return;
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createtradebtc2btcry(String[] split) {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            System.out.println("clientconnection@586");
            tradebtcry2btc = "";
            if (tradeAccount.isEmpty() || tradeAccount2.isEmpty()) {
                System.out.println("clientconnection@589");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            if (!tradebtc2btcry.isEmpty()) {
                System.out.println("clientconnection@595");
                this.server.send("E_ERROR");
                this.server.close();
                return;
            }
            System.out.println("clientconnection@600");
            this.server.send("E_ERROR");
            this.server.close();
            double amount = 0;
            double price = 0;
            double balance = bitcoinrpc.getBalance(tradeAccount);
            try {
                System.out.println("clientconnection@607");
                amount = Double.parseDouble(split[1]);
                price = Double.parseDouble(split[2]);
                if (amount <= 0 || price <= 0 || price > balance) {
                    System.out.println("clientconnection@611");
                    throw new Exception();
                }
            } catch (Exception ex) {
                System.out.println("clientconnection@615");
                tradebtcry2btc = "";
                tradebtc2btcry = "";
                return;
            }
            tradebtc2btcry = amount + ",," + price + ",," + tradeAccount + ",," + tradeAccount2;
            tradebtcry2btc = "";
            this.saveClient();
            return;
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
