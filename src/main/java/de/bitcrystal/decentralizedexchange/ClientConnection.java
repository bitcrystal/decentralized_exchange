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

    private static String tradeAccount = "";
    private static String tradeAccount2 = "";
    private static String tradebtcry2btc = "";
    private static String tradebtc2btcry = "";
    private static String currentTradeAddress = "";
    private static String currentTradePubKey = "";
    private static String tradeWithAddress = "";
    private static boolean isSynced = false;
    private static boolean isStarted = false;
    private static boolean isEnded = false;
    private static boolean allok = false;
    private static boolean isEndedMe = false;
    private static boolean isEndedOther = false;
    private static String tradeAccountMultisigAddressBitcoin = "";
    private static String tradeAccountMultisigAddressBitcrystal = "";
    private static String tradeAccount2MultisigAddressBitcoin = "";
    private static String tradeAccount2MultisigAddressBitcrystal = "";
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
            DebugClient.println("mausge");
            if (command == null || command.isEmpty()) {
                this.server.send("E_ERROR");
                this.server.close();
                DebugClient.println("mausssssssge");
                setLastCommandStatus(false);
                return;
            }
            if (!command.contains(" ")) {
                command = command + " ";
            }
            String[] split = command.split(" ");
            DebugClient.println(split.length);
            DebugClient.println(split[0]);
            if (split.length < 1) {
                this.server.send("E_ERROR");
                this.server.close();
                DebugClient.println("mausge");
                DebugClient.println("mauasdadaaddasge");
                setLastCommandStatus(false);
                return;
            }
            switch (split.length) {
                case 1: {
                    if (split[0].equalsIgnoreCase("ADD")) {
                        DebugClient.println("add open");
                        add();
                        DebugClient.println("add close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("GETCURRENTTRADERADDRESS")) {
                        System.out.println(currentTradeAddress);
                        this.server.close();
                        setLastCommandStatus(true);
                        return;
                    }

                    if (split[0].equalsIgnoreCase("GETCURRENTTRADEWITHADDRESS")) {
                        System.out.println(tradeWithAddress);
                        this.server.close();
                        setLastCommandStatus(true);
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
                        setLastCommandStatus(true);
                        return;
                    }

                    if (split[0].equalsIgnoreCase("TRADEABORT")) {
                        DebugClient.println("tradeabort open");
                        tradeabort();
                        DebugClient.println("tradeabort close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("SYNCTRADE")) {
                        DebugClient.println("synctrade open");
                        synctrade();
                        DebugClient.println("synctrade close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("STARTTRADE")) {
                        DebugClient.println("starttrade open");
                        starttrade();
                        DebugClient.println("starttrade close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("ENDTRADEME")) {
                        DebugClient.println("endtrademe open");
                        endtrademe();
                        DebugClient.println("endtrademe close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("ENDTRADEOTHER")) {
                        DebugClient.println("endtradeother open");
                        endtradeother();
                        DebugClient.println("endtradeother close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("ENDTRADE")) {
                        DebugClient.println("endtrade open");
                        endtrade();
                        DebugClient.println("endtrade close");
                        return;
                    }
                }
                break;

                case 2: {
                    if (split[0].equalsIgnoreCase("TRADEWITH")) {
                        DebugClient.println("tradewith open");
                        tradewith(split);
                        DebugClient.println("tradewith close");
                        return;
                    }
                }
                break;

                case 3: {
                    if (split[0].equalsIgnoreCase("CREATETRADEBTCRY2BTC")) {
                        DebugClient.println("createbtcry2btc open");
                        createtradebtcry2btc(split);
                        DebugClient.println("createbtcry2btc close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("CREATETRADEBTC2BTCRY")) {
                        DebugClient.println("createtradebtc2ntcry open");
                        createtradebtc2btcry(split);
                        DebugClient.println("createtradebtc2btcry close");
                        return;
                    }
                }
                break;
            }
        } catch (Exception ex) {
            DebugClient.println("clientconnection@627");
            this.server.send("E_ERROR");
            this.server.close();
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            setLastCommandStatus(false);
            return;
        }
    }

    private synchronized void initClient() {
        DebugClient.println("test");
        clientJSON = this.server.loadJSONObject("client", "", "client.properties");
        DebugClient.println("cool");
        if (clientJSON == null) {
            try {
                DebugClient.println("coolvvv");
                JSONObject json = new JSONObject();
                json.put("currentTradeAddress", "");
                json.put("currentTradePubKey", "");
                json.put("isEnded", "");
                json.put("isStarted", "");
                json.put("isSynced", "");
                json.put("tradeAccount", "");
                json.put("tradeAccount2", "");
                json.put("tradeWithAddress", "");
                json.put("tradebtc2btcry", "");
                json.put("tradebtcry2ntc", "");
                DebugClient.println("nein");
                this.server.saveJSONObject(json, "client", "", "client.properties");
                DebugClient.println("hier");
                clientJSON = json;
            } catch (JSONException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                DebugClient.println("coolffff");
                currentTradeAddress = clientJSON.getString("currentTradeAddress");
            } catch (JSONException ex) {
                DebugClient.println("coolddddd");
                currentTradeAddress = "";
            }
            try {
                DebugClient.println("coolfffddf");
                currentTradePubKey = clientJSON.getString("currentTradePubKey");
            } catch (JSONException ex) {
                DebugClient.println("coolddssssddd");
                currentTradePubKey = "";
            }
            try {
                tradeWithAddress = clientJSON.getString("tradeWithAddress");
                DebugClient.println("cxxxxxxxxool");
            } catch (JSONException ex) {
                tradeWithAddress = "";
                DebugClient.println("ssssssssssscool");
            }
            try {
                tradeAccount = clientJSON.getString("tradeAccount");
                DebugClient.println("xxadadaadcool");
            } catch (JSONException ex) {
                tradeAccount = "";
                DebugClient.println("ddasfasdgcool");
            }
            try {
                tradeAccount2 = clientJSON.getString("tradeAccount2");
                DebugClient.println("cxyxcaysacxycool");
            } catch (JSONException ex) {
                tradeAccount2 = "";
                DebugClient.println("cofdgfwe43eewol");
            }
            try {
                tradebtc2btcry = clientJSON.getString("tradebtc2btcry");
                DebugClient.println("cossafdasfdcdsdfol");
            } catch (JSONException ex) {
                tradebtc2btcry = "";
                DebugClient.println("weqeqqeqcool");
            }
            try {
                tradebtcry2btc = clientJSON.getString("tradebtcry2btc");
                DebugClient.println("cdycycycyool");
            } catch (JSONException ex) {
                tradebtcry2btc = "";
                DebugClient.println("cooaddadasdasl");
            }
            try {
                isEnded = clientJSON.getBoolean("isEnded");
                DebugClient.println("coadadadadaol");
            } catch (JSONException ex) {
                isEnded = false;
                DebugClient.println("coasdadadol");
            }
            try {
                isSynced = clientJSON.getBoolean("isSynced");
                DebugClient.println("codadadaadadadadadol");
            } catch (JSONException ex) {
                isSynced = false;
                DebugClient.println("daaadadadadadadaddcool");
            }
            try {
                isStarted = clientJSON.getBoolean("isStarted");
                DebugClient.println("caddaadadadool");
            } catch (JSONException ex) {
                isStarted = false;
                DebugClient.println("cossssssol");
            }
        }
    }

    private synchronized void saveClient() {
        DebugClient.println("clientconnection@727");
        JSONObject json = this.server.loadJSONObject("client", "", "client.properties");
        if (json == null) {
            return;
        }
        try {
            DebugClient.println("clientconnection@733");
            json.put("currentTradePubKey", currentTradePubKey);
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
            DebugClient.println("clientconnection@745");
        } catch (JSONException ex) {
            DebugClient.println("clientconnection@747");
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        DebugClient.println("end");
    }

    private void add() {
        try {
            DebugClient.println("clientconnection@79");
            if (!currentTradeAddress.isEmpty()) {
                this.server.send("E_ERROR");
                this.server.close();
                DebugClient.println("mausasdadadadae");
                setLastCommandStatus(false);
                return;
            }
            DebugClient.println("clientconnection@85");
            DebugClient.println("maadadadadadadadadusge");
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            String newAddress = bitcoinrpc.getNewAddress();
            currentTradeAddress = newAddress;
            DebugClient.println("mauadadadadadsge");
            DebugClient.println("clientconnection@91");
            this.saveClient();
            DebugClient.println("madaadadadaadaadadadusge");
            String pubKey = bitcoinrpc.getPubKey(newAddress);
            currentTradePubKey = pubKey;
            String privKey = bitcoinrpc.getPrivKey(newAddress);
            if (!bitcrystalrpc.addressexists(newAddress)) {
                bitcrystalrpc.importPrivKey(privKey);
            }
            DebugClient.println(privKey);
            DebugClient.println(newAddress);
            this.server.send("add," + pubKey);
            DebugClient.println("mausssdadsasdasdasdge");
            String recv = this.server.recvLight();
            DebugClient.println("mausasadsasdssge");
            DebugClient.println(recv);
            DebugClient.println("cool");
            this.server.close();
            this.saveClient();
            setLastCommandStatus(true);
            return;
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void synctrade() {
        if (isSynced) {
            DebugClient.println("clientconnection@123");
            this.server.send("E_ERROR");
            this.server.close();
            setLastCommandStatus(false);
            return;
        }
        if (tradebtc2btcry.isEmpty() && tradebtcry2btc.isEmpty()) {
            DebugClient.println("clientconnection@129");
            this.server.send("E_ERROR");
            this.server.close();
            setLastCommandStatus(false);
            return;
        }
        if (!tradebtc2btcry.isEmpty() && !tradebtcry2btc.isEmpty()) {
            DebugClient.println("clientconnection@135");
            this.server.send("E_ERROR");
            this.server.close();
            setLastCommandStatus(false);
            return;
        }
        if (!tradebtc2btcry.isEmpty()) {
            DebugClient.println("clientconnection@141");
            this.server.send("synctrade;btc2btcry,," + tradebtc2btcry);
            String recv = this.server.recvLight();
            if (recv.equals("E_ERROR")) {
                DebugClient.println("clientconnection@145");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            this.server.send("AWESOME!");
            this.server.close();
            isSynced = true;
            this.saveClient();
            setLastCommandStatus(true);
            return;
        }

        if (!tradebtcry2btc.isEmpty()) {
            DebugClient.println("clientconnection@157");
            this.server.send("synctrade;btcry2btc,," + tradebtcry2btc);
            String recv = this.server.recvLight();
            DebugClient.println(recv);
            if (recv.equals("E_ERROR")) {
                DebugClient.println("clientconnection@162");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            this.server.send("AWESOME!");
            this.server.close();
            isSynced = true;
            this.saveClient();
            setLastCommandStatus(true);
            return;
        }
    }

    private void endtrade() {
        this.server.send("endtrade");
        String recv = this.server.recvLight();
        if (recv.equals("E_ERROR")) {
            this.server.close();
            setLastCommandStatus(false);
            isEnded = false;
            return;
        }
        if (recv.equals("ALL_OK")) {
            DebugClient.println("YEAH I'VE WON!!!!!!!!!!!!!!!!!!!11");
            this.server.close();
            setLastCommandStatus(true);
            isEnded = true;
            return;
        }
    }

    private void tradeabort() {
        if ((currentTradePubKey.isEmpty() || currentTradeAddress.isEmpty() || isEndedMe || isEndedOther || isSynced || isStarted) && isEnded == false) {
            this.server.close();
            setLastCommandStatus(true);
            return;
        }
        //String proofOfWorkSHA1 = HashFunctions.getProofOfWorkSHA1(1, currentTradePubKey);
        //System.out.println(proofOfWorkSHA1);
        currentTradeAddress = "";
        tradeAccount = "";
        tradeAccount2 = "";
        tradebtc2btcry = "";
        tradebtcry2btc = "";
        String temp = currentTradePubKey;
        currentTradePubKey = "";
        tradeWithAddress = "";
        isEndedMe = false;
        isEndedOther = false;
        isSynced = false;
        isEnded = false;
        isStarted = false;
        //this.server.send("tradeabort;;" + temp + ";;" + proofOfWorkSHA1);
        this.server.send("tradeabort;;" + temp);
        this.server.recvLight();
        this.server.close();
        this.saveClient();
        setLastCommandStatus(true);
        return;
    }

    private void starttrade() {
        DebugClient.println("clientconnection@175");
        if (!isSynced) {
            DebugClient.println("clientconnection@177");
            this.server.send("E_ERROR");
            this.server.close();
            setLastCommandStatus(false);
            return;
        }
        DebugClient.println("clientconnection@182");
        this.server.send("starttrade");
        DebugClient.println("clientconnection@184");
        String recv = this.server.recvLight();
        DebugClient.println(recv);
        DebugClient.println("clientconnection@187");
        if (recv.equals("E_ERROR")) {
            DebugClient.println("clientconnection@185");
            this.server.send("E_ERROR");
            this.server.close();
            setLastCommandStatus(false);
            return;
        }
        DebugClient.println("clientconnection@194");
        this.server.send(":)");
        this.server.close();
        isStarted = true;
        this.saveClient();
        setLastCommandStatus(true);
        return;
    }

    private void endtrademe() {
        try {
            DebugClient.println("clientconnection@201");
            if (!isStarted) {
                DebugClient.println("clientconnection@203");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            DebugClient.println("clientconnection@208");
            this.server.send("endtrademe;" + tradeAccount);
            DebugClient.println("clientconnection@210");
            String recv = this.server.recvLight();
            DebugClient.println(recv);
            DebugClient.println("clientconnection@213");
            if (recv.equals("E_ERROR")) {
                DebugClient.println("clientconnection@215");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (!tradebtc2btcry.isEmpty()) {
                DebugClient.println("clientconnection@221");
                if (!recv.startsWith("btc2btcry;;")) {
                    DebugClient.println("clientconnection@223");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("clientconnection@228");
                if (!recv.contains(";;")) {
                    DebugClient.println("clientconnection@229");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String[] split2 = recv.split(";;");
                DebugClient.println("clientconnection@236");
                if (split2.length != 5) {
                    DebugClient.println("clientconnection@238");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String myTradeAccount = split2[1];
                String myTradeWithAddress = split2[2];
                String myPrice = split2[3];
                String myTransaction = split2[4];
                if (!myTradeAccount.equals(tradeAccount)) {
                    DebugClient.println("clientconnection@248");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (!myTradeWithAddress.equals(tradeWithAddress)) {
                    DebugClient.println("clientconnection@254");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                double price = 0;
                try {
                    price = Double.parseDouble(myPrice);
                    if (price <= 0) {
                        DebugClient.println("clientconnection@263");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    DebugClient.println("clientconnection@267");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println(tradebtc2btcry);
                String[] split1 = tradebtc2btcry.split(",,");
                DebugClient.println(split1[0]);
                DebugClient.println(split1[1]);
                double thisprice = 0;
                try {
                    DebugClient.println("clientconnection@275");
                    DebugClient.println(split1[1]);
                    thisprice = Double.parseDouble(split1[1]);
                    if (thisprice <= 0 || price != thisprice) {
                        DebugClient.println("clientconnection@278");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    DebugClient.println("clientconnection@282");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }

                Object[] values = {tradeAccount, tradeWithAddress, price, 0.00};
                String createrawtransaction_multisig = bitcoinrpc.createrawtransaction_multisig(values);
                DebugClient.println(myTransaction);
                DebugClient.println(createrawtransaction_multisig);
                if (!bitcoinrpc.testtransactionequals_multisig(createrawtransaction_multisig, myTransaction)) {
                    DebugClient.println("clientconnection@290");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("clientconnection@295");
                JsonObject decodeRawTransactionMultisig = null;
                try {
                    DebugClient.println("clientconnection@297");
                    decodeRawTransactionMultisig = bitcoinrpc.decodeRawTransactionMultisig(createrawtransaction_multisig);
                } catch (Exception ex2) {
                    DebugClient.println("clientconnection@301");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String signrawtransaction_multisig = bitcoinrpc.signrawtransaction_multisig(createrawtransaction_multisig, 1);
                this.server.sendLight(signrawtransaction_multisig);
                String recv1 = this.server.recvLight();
                if (recv1.equals("E_ERROR")) {
                    DebugClient.println("clientconnection@310");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (recv1.equals("ALL_OK")) {
                    DebugClient.println("clientconnection@316");
                    this.server.close();
                    setLastCommandStatus(true);
                    return;
                }
            } else if (!tradebtcry2btc.isEmpty()) {
                DebugClient.println("clientconnection@321");
                if (!recv.startsWith("btcry2btc;;")) {
                    DebugClient.println("clientconnection@323");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (!recv.contains(";;")) {
                    DebugClient.println("clientconnection@329");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String[] split2 = recv.split(";;");
                DebugClient.println("clientconnection@335");
                if (split2.length != 5) {
                    DebugClient.println("clientconnection@337");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String myTradeAccount = split2[1];
                String myTradeWithAddress = split2[2];
                String myPrice = split2[3];
                String myTransaction = split2[4];
                if (!myTradeAccount.equals(tradeAccount)) {
                    DebugClient.println("clientconnection@347");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (!myTradeWithAddress.equals(tradeWithAddress)) {
                    DebugClient.println("clientconnection@353");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                double price = 0;
                try {
                    DebugClient.println("clientconnection@360");
                    price = Double.parseDouble(myPrice);
                    if (price <= 0) {
                        DebugClient.println("clientconnection@363");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    DebugClient.println("clientconnection@367");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String[] split1 = tradebtcry2btc.split(",,");
                double thisprice = 0;
                try {
                    DebugClient.println("clientconnection@375");
                    DebugClient.println(split1[1]);
                    thisprice = Double.parseDouble(split1[1]);
                    if (thisprice <= 0 || price != thisprice) {
                        DebugClient.println("clientconnection@378");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    DebugClient.println("clientconnection@381");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                Object[] values = {tradeAccount, tradeWithAddress, price, 0.00};
                String createrawtransaction_multisig = bitcrystalrpc.createrawtransaction_multisig(values);
                if (!bitcrystalrpc.testtransactionequals_multisig(createrawtransaction_multisig, myTransaction)) {
                    DebugClient.println("clientconnection@390");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                JsonObject decodeRawTransactionMultisig = null;
                try {
                    DebugClient.println("clientconnection@397");
                    decodeRawTransactionMultisig = bitcrystalrpc.decodeRawTransactionMultisig(createrawtransaction_multisig);
                } catch (Exception ex2) {
                    DebugClient.println("clientconnection@400");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String signrawtransaction_multisig = bitcrystalrpc.signrawtransaction_multisig(createrawtransaction_multisig);
                this.server.sendLight(signrawtransaction_multisig);
                String recv1 = this.server.recvLight();
                DebugClient.println("clientconnection@408");
                if (recv1.equals("E_ERROR")) {
                    DebugClient.println("clientconnection@410");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (recv1.equals("ALL_OK")) {
                    DebugClient.println("clientconnection@416");
                    this.server.close();
                    this.saveClient();
                    setLastCommandStatus(true);
                    isEndedMe = true;
                    return;
                }
            }
            setLastCommandStatus(false);
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            setLastCommandStatus(false);
        }
    }

    private void endtradeother() {
        try {
            DebugClient.println("clientconnection@424");
            if (!isStarted) {
                DebugClient.println("clientconnection@362");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            DebugClient.println("clientconnection@367");
            this.server.send("endtradeother;" + tradeAccount);
            DebugClient.println("clientconnection@369");
            String recv = this.server.recvLight();
            DebugClient.println(recv);
            DebugClient.println("clientconnection@370");
            if (recv.equals("E_ERROR")) {
                DebugClient.println("clientconnection@374");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (!tradebtc2btcry.isEmpty()) {
                DebugClient.println("clientconnection@380");
                String[] split1 = tradebtc2btcry.split(",,");
                JsonObject decodeRawTransactionMultisig1 = null;
                try {
                    DebugClient.println("clientconnection@385");
                    DebugClient.println(recv);
                    decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(recv);
                    DebugClient.println("clientconnection@720");
                    DebugClient.println("clientconnection@721");
                    if (!decodeRawTransactionMultisig1.has("complete")) {
                        DebugClient.println("clientconnection@389");
                        this.server.send("E_ERROR");
                        this.server.close();
                        setLastCommandStatus(false);
                        return;
                    }
                    DebugClient.println("clientconnection@728");
                    String asString = decodeRawTransactionMultisig1.get("toaddress").getAsString();
                    double asDouble = decodeRawTransactionMultisig1.get("amount").getAsDouble();
                    String currencyprefix = decodeRawTransactionMultisig1.get("currencyprefix").getAsString();
                    DebugClient.println("clientconnection@732");
                    if (!(("" + asDouble).equals(split1[0]))
                            || //!currencyprefix.equals("BTCRY") ||
                            !asString.equals(currentTradeAddress)) {
                        DebugClient.println("clientconnection@398");
                        this.server.send("E_ERROR");
                        this.server.close();
                        setLastCommandStatus(false);
                        return;
                    }
                } catch (Exception ex2) {
                    DebugClient.println("clientconnection@404");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String signrawtransaction_multisig1 = bitcrystalrpc.signrawtransaction_multisig(recv, 1);
                try {
                    DebugClient.println("clientconnection@411");
                    DebugClient.println(signrawtransaction_multisig1);
                    decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                    //JsonElement get = decodeRawTransactionMultisig1.get("complete");
                   /* if (get.getAsBoolean() == true) {
                    DebugClient.println("clientconnection@415");
                    throw new Exception();
                    }*/
                } catch (Exception ex2) {
                    DebugClient.println("clientconnection@419");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("clientconnection@424");
                this.server.send(signrawtransaction_multisig1);
                DebugClient.println("clientconnection@426");
                this.server.recv();
                this.server.close();
                setLastCommandStatus(true);
                return;
            } else if (!tradebtcry2btc.isEmpty()) {
                DebugClient.println("clientconnection@430");
                DebugClient.println(recv);
                DebugClient.println("clientconnection@432");
                String[] split1 = tradebtcry2btc.split(",,");
                JsonObject decodeRawTransactionMultisig1 = null;
                try {
                    DebugClient.println("clientconnection@437");
                    decodeRawTransactionMultisig1 = bitcoinrpc.decodeRawTransactionMultisig(recv);
                    if (!decodeRawTransactionMultisig1.has("complete")) {
                        DebugClient.println("clientconnection@440");
                        this.server.send("E_ERROR");
                        this.server.close();
                        setLastCommandStatus(false);
                        return;
                    }
                    String asString = decodeRawTransactionMultisig1.get("toaddress").getAsString();
                    double asDouble = decodeRawTransactionMultisig1.get("amount").getAsDouble();
                    String currencyprefix = decodeRawTransactionMultisig1.get("currencyprefix").getAsString();
                    if (!(("" + asDouble).equals(split1[0]))
                            //|| !currencyprefix.equals("BTC") 
                            || !asString.equals(currentTradeAddress)) {
                        DebugClient.println("clientconnection@449");
                        this.server.send("E_ERROR");
                        this.server.close();
                        setLastCommandStatus(false);
                        return;
                    }
                } catch (Exception ex2) {
                    DebugClient.println("clientconnection@455");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("clientconnection@460");
                String signrawtransaction_multisig1 = bitcoinrpc.signrawtransaction_multisig(recv);
                try {
                    DebugClient.println("clientconnection@462");
                    decodeRawTransactionMultisig1 = bitcoinrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                    JsonElement get = decodeRawTransactionMultisig1.get("complete");
                    if (get.getAsBoolean() == true) {
                        DebugClient.println("clientconnection@466");
                        throw new Exception();
                    }
                } catch (Exception ex2) {
                    DebugClient.println("clientconnection@470");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("clientconnection@476");
                this.server.sendLight(signrawtransaction_multisig1);
                DebugClient.println("clientconnection@478");
                this.server.recvLight();
                DebugClient.println("clientconnection@480");
                this.server.close();
                this.saveClient();
                setLastCommandStatus(true);
                isEndedOther = true;
                return;
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            setLastCommandStatus(false);
        }
    }

    private void tradewith(String[] split) {
        try {
            DebugClient.println("sssssad");
            if (!tradeAccount.isEmpty() && !tradeAccount2.isEmpty()) {
                this.server.send("E_ERROR");
                this.server.close();
                DebugClient.println("ssasdas");
                setLastCommandStatus(false);
                return;
            }
            DebugClient.println("asljkfjcyklnjkcbgzbdsyxjbvcbcb");
            this.server.send("tradewith," + split[1]);
            String recv = this.server.recvLight();
            this.server.close();
            DebugClient.println(recv);
            DebugClient.println("ddddff");
            if (recv.equals("E_ERROR")) {
                this.server.close();
                DebugClient.println("asdasksdjkjasdkasjskdjsd");
                setLastCommandStatus(false);
                return;
            }
            if (!recv.contains(",,")) {
                this.server.close();
                DebugClient.println("xdadjkjsklhdjlfhjaskbhfjskdhjkdsfh");
                setLastCommandStatus(false);
                return;
            }

            String[] split1 = recv.split(",,");
            if (split1.length != 3) {
                this.server.close();
                DebugClient.println("skjsfkljdskljfdkljdsikljsdfkljdkl");
                setLastCommandStatus(false);
                return;
            }

            tradeWithAddress = split1[2];
            tradeAccount = currentTradeAddress + "," + tradeWithAddress;
            tradeAccount2 = tradeWithAddress + "," + currentTradeAddress;
            Object[] values1 = {split1[0], tradeAccount};
            Object[] values2 = {split1[1], tradeAccount};
            Object[] values3 = {split1[0], tradeAccount2};
            Object[] values4 = {split1[1], tradeAccount2};
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            try {
                DebugClient.println("dddddd");
                bitcoinrpc.addmultisigaddressex(values1);
                bitcrystalrpc.addmultisigaddressex(values2);
                bitcoinrpc.addmultisigaddressex(values3);
                bitcrystalrpc.addmultisigaddressex(values4);
            } catch (Exception ex2) {
            }
            /*
            try {
            if (!bitcoinrpc.accountexists(tradeAccount)) {
            DebugClient.println("skfjkdjkldjkldnx,mvnc");
            bitcoinrpc.addmultisigaddressex(values1);
            bitcrystalrpc.addmultisigaddressex(values2);
            }
            
            if (!bitcoinrpc.accountexists(tradeAccount2)) {
            DebugClient.println("skjflkxadnklmxcvnbgds");
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
            DebugClient.println("djdnvncncnnccc");
            this.server.close();
            this.saveClient();
            setLastCommandStatus(true);
            return;
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            setLastCommandStatus(false);
        }
    }

    private void createtradebtcry2btc(String[] split) {
        try {
            DebugClient.println("clientconnection@545");
            tradebtc2btcry = "";
            if (tradeAccount.isEmpty() || tradeAccount2.isEmpty()) {
                DebugClient.println("clientconnection@548");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            if (!tradebtcry2btc.isEmpty()) {
                DebugClient.println("clientconnection@553");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            DebugClient.println("clientconnection@559");
            this.server.send("E_ERROR");
            this.server.close();
            double amount = 0;
            double price = 0;
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            double balance = bitcrystalrpc.getBalance(tradeAccount);
            try {
                DebugClient.println("clientconnection@566");
                amount = Double.parseDouble(split[1]);
                price = Double.parseDouble(split[2]);
                if (amount <= 0 || price <= 0 || price > balance) {
                    DebugClient.println("clientconnection@570");
                    throw new Exception();
                }
            } catch (Exception ex) {
                DebugClient.println("clientconnection@574");
                tradebtcry2btc = "";
                tradebtc2btcry = "";
                setLastCommandStatus(false);
                return;
            }

            tradebtcry2btc = amount + ",," + price + ",," + tradeAccount + ",," + tradeAccount2;
            tradebtc2btcry = "";
            this.saveClient();
            DebugClient.println("clientconnection@582");
            setLastCommandStatus(true);
            return;
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            setLastCommandStatus(false);
        }
    }

    private void createtradebtc2btcry(String[] split) {
        try {
            DebugClient.println("clientconnection@586");
            tradebtcry2btc = "";
            if (tradeAccount.isEmpty() || tradeAccount2.isEmpty()) {
                DebugClient.println("clientconnection@589");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            if (!tradebtc2btcry.isEmpty()) {
                DebugClient.println("clientconnection@595");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            DebugClient.println("clientconnection@600");
            this.server.send("E_ERROR");
            this.server.close();
            double amount = 0;
            double price = 0;
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
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
                setLastCommandStatus(false);
                return;
            }
            tradebtc2btcry = amount + ",," + price + ",," + tradeAccount + ",," + tradeAccount2;
            tradebtcry2btc = "";
            this.saveClient();
            setLastCommandStatus(true);
            return;
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            setLastCommandStatus(false);
        }
    }

    public static String getTradeAccount() {
        return tradeAccount;
    }

    public static String getTradeAccount2() {
        return tradeAccount2;
    }

    public static String getTradeAccountMultisigAddressForBitcoin() {
        if (!tradeAccountMultisigAddressBitcoin.isEmpty()) {
            return tradeAccountMultisigAddressBitcoin;
        }
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (tradeAccount2 == null || tradeAccount2.isEmpty()) {
                return "";
            }
            String string = bitcoinrpc.getmultisigaddressofaddressoraccount(tradeAccount2);
            if (string == null || string.isEmpty()) {
                return "";
            }
            tradeAccountMultisigAddressBitcoin = string;
            return string;
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getTradeAccountMultisigAddressForBitcrystal() {
        if (!tradeAccountMultisigAddressBitcrystal.isEmpty()) {
            return tradeAccountMultisigAddressBitcrystal;
        }
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            if (tradeAccount2 == null || tradeAccount2.isEmpty()) {
                return "";
            }
            String string = bitcrystalrpc.getmultisigaddressofaddressoraccount(tradeAccount2);
            if (string == null || string.isEmpty()) {
                return "";
            }
            tradeAccountMultisigAddressBitcrystal = string;
            return string;
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getTradeAccount2MultisigAddressForBitcoin() {
        if (!tradeAccount2MultisigAddressBitcoin.isEmpty()) {
            return tradeAccount2MultisigAddressBitcoin;
        }
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (tradeAccount2 == null || tradeAccount2.isEmpty()) {
                return "";
            }
            String string = bitcoinrpc.getmultisigaddressofaddressoraccount(tradeAccount2);
            if (string == null || string.isEmpty()) {
                return "";
            }
            tradeAccount2MultisigAddressBitcoin = string;
            return string;
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getTradeAccount2MultisigAddressForBitcrystal() {
        if (!tradeAccount2MultisigAddressBitcrystal.isEmpty()) {
            return tradeAccount2MultisigAddressBitcrystal;
        }
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            if (tradeAccount2 == null || tradeAccount2.isEmpty()) {
                return "";
            }
            String string = bitcrystalrpc.getmultisigaddressofaddressoraccount(tradeAccount2);
            if (string == null || string.isEmpty()) {
                return "";
            }
            tradeAccount2MultisigAddressBitcrystal = string;
            return string;
        } catch (Exception ex) {
            return "";
        }
    }

    public static double getBitcoinBalanceTradeAccount() {
        if (tradeAccountMultisigAddressBitcoin.isEmpty()) {
            return -1;
        }
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            return bitcoinrpc.getBalance(tradeAccountMultisigAddressBitcoin);
        } catch (Exception ex) {
            return -1;
        }
    }

    public static double getBitcrystalBalanceTradeAccount() {
        if (tradeAccountMultisigAddressBitcrystal.isEmpty()) {
            return -1;
        }
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            return bitcrystalrpc.getBalance(tradeAccountMultisigAddressBitcrystal);
        } catch (Exception ex) {
            return -1;
        }
    }

    public static double getBitcoinBalanceTradeAccount2() {
        if (tradeAccount2MultisigAddressBitcoin.isEmpty()) {
            return -1;
        }
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            return bitcoinrpc.getBalance(tradeAccount2MultisigAddressBitcoin);
        } catch (Exception ex) {
            return -1;
        }
    }

    public static double getBitcrystalBalanceTradeAccount2() {
        if (tradeAccount2MultisigAddressBitcrystal.isEmpty()) {
            return -1;
        }
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            return bitcrystalrpc.getBalance(tradeAccount2MultisigAddressBitcrystal);
        } catch (Exception ex) {
            return -1;
        }
    }

    public static String getTradeBtc2btcry() {
        return tradebtc2btcry;
    }

    public static String getTradeBtcry2btc() {
        return tradebtcry2btc;
    }

    public static String getCurrentTradeAddress() {
        return currentTradeAddress;
    }

    public static String getCurrentTradePubKey() {
        return currentTradePubKey;
    }

    public static String getCurrentTradeWithAddress() {
        return currentTradeAddress;
    }

    public static boolean isSynced() {
        return isSynced;
    }

    public static boolean isStarted() {
        return isStarted;
    }

    public static boolean isEnded() {
        return isEnded;
    }

    public static boolean getLastCommandStatus() {
        boolean temp = allok;
        allok = false;
        return temp;
    }

    public static void setLastCommandStatus(boolean set) {
        allok = set;
    }
}
