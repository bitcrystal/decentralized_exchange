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
import javax.swing.JOptionPane;
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
    private static String txsendhashTradeAccountBitcoin = "";
    private static String txsendhashTradeAccountBitcrystal = "";
    private static String txsendhashTradeAccount2Bitcoin = "";
    private static String txsendhashTradeAccount2Bitcrystal = "";
    private static double bitcoinTradeAccountBalance = 0;
    private static double bitcrystalTradeAccountBalance = 0;
    private static double bitcoinTradeAccount2Balance = 0;
    private static double bitcrystalTradeAccount2Balance = 0;
    private static double fake1 = 0;
    private static double fake2 = 0;
    private TCPClientSecurity server;
    private String command;
    private static JSONObject clientJSON = null;
    private static boolean isTradeAborted = false;

    public ClientConnection(TCPClient server, String command) {
        DebugClient.println("@clienctconnection 54");
        this.server = DecentralizedExchange.getSecurityClient(server);
        DebugClient.println("@clienctconnection 55");
        this.command = command;
        DebugClient.println("@clienctconnection 57");
        if (clientJSON == null) {
            this.initClient();
        } else {
            this.saveClient();
        }
        DebugClient.println("@clienctconnection 59");

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

                    if (split[0].equalsIgnoreCase("UPDATEBALANCE")) {
                        DebugClient.println("updatebalance open");
                        updatebalance();
                        DebugClient.println("updatebalance close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("GETBALANCE")) {
                        DebugClient.println("getbalance open");
                        getbalance();
                        DebugClient.println("getbalance close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("UPDATEBALANCEBITCOIN")) {
                        DebugClient.println("updatebalancebitcoin open");
                        updatebalancebitcoin();
                        DebugClient.println("updatebalancebitcoin close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("UPDATEBALANCEBITCOIN2")) {
                        DebugClient.println("updatebalancebitcoin2 open");
                        updatebalancebitcoin2();
                        DebugClient.println("updatebalancebitcoin2 close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("GETBALANCEBITCOIN")) {
                        DebugClient.println("getbalancebitcoin open");
                        getbalancebitcoin();
                        DebugClient.println("getbalancebitcoin close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("UPDATEBALANCEBITCRYSTAL")) {
                        DebugClient.println("updatebalancebitcrystal open");
                        updatebalancebitcrystal();
                        DebugClient.println("updatebalancebitcrystal close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("UPDATEBALANCEBITCRYSTAL2")) {
                        DebugClient.println("updatebalancebitcrystal2 open");
                        updatebalancebitcrystal2();
                        DebugClient.println("updatebalancebitcrystal2 close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("GETBALANCEBITCRYSTAL")) {
                        DebugClient.println("getbalancebitcrystal open");
                        getbalancebitcrystal();
                        DebugClient.println("getbalancebitcrystal close");
                        return;
                    }

                    if (split[0].equalsIgnoreCase("TRADERESET")) {
                        DebugClient.println("tradereset open");
                        tradereset();
                        DebugClient.println("tradereset close");
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
        clientJSON = this.server.loadJSON("client", "", "client.properties");
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
                json.put("tradebtcry2btc", "");
                json.put("txsendhashTradeAccountBitcoin", "");
                json.put("txsendhashTradeAccountBitcrystal", "");
                json.put("txsendhashTradeAccount2Bitcoin", "");
                json.put("txsendhashTradeAccount2Bitcrystal", "");
                json.put("bitcoinTradeAccountBalance", "");
                json.put("bitcrystalTradeAccountBalance", "");
                json.put("bitcoinTradeAccount2Balance", "");
                json.put("bitcrystalTradeAccount2Balance", "");
                DebugClient.println("nein");
                this.server.saveJSON(json, "client", "", "client.properties");
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
                txsendhashTradeAccountBitcoin = clientJSON.getString("txsendhashTradeAccountBitcoin");
                DebugClient.println("caddaadadadool");
            } catch (JSONException ex) {
                txsendhashTradeAccountBitcoin = "";
                DebugClient.println("cossssssol");
            }
            try {
                txsendhashTradeAccount2Bitcoin = clientJSON.getString("txsendhashTradeAccount2Bitcoin");
                DebugClient.println("caddaadadadool");
            } catch (JSONException ex) {
                txsendhashTradeAccount2Bitcoin = "";
                DebugClient.println("cossssssol");
            }
            try {
                txsendhashTradeAccountBitcrystal = clientJSON.getString("txsendhashTradeAccountBitcrystal");
                DebugClient.println("caddaadadadool");
            } catch (JSONException ex) {
                txsendhashTradeAccountBitcrystal = "";
                DebugClient.println("cossssssol");
            }
            try {
                txsendhashTradeAccount2Bitcrystal = clientJSON.getString("txsendhashTradeAccount2Bitcrystal");
                DebugClient.println("caddaadadadool");
            } catch (JSONException ex) {
                txsendhashTradeAccount2Bitcrystal = "";
                DebugClient.println("cossssssol");
            }
            try {
                bitcoinTradeAccountBalance = clientJSON.getDouble("bitcoinTradeAccountBalance");
                DebugClient.println("caddaadadadool");
            } catch (JSONException ex) {
                bitcoinTradeAccountBalance = 0;
                DebugClient.println("cossssssol");
            }
            try {
                bitcoinTradeAccount2Balance = clientJSON.getDouble("bitcoinTradeAccount2Balance");
                DebugClient.println("caddaadadadool");
            } catch (JSONException ex) {
                bitcoinTradeAccount2Balance = 0;
                DebugClient.println("cossssssol");
            }
            try {
                bitcrystalTradeAccountBalance = clientJSON.getDouble("bitcrystalTradeAccountBalance");
                DebugClient.println("caddaadadadool");
            } catch (JSONException ex) {
                bitcrystalTradeAccountBalance = 0;
                DebugClient.println("cossssssol");
            }
            try {
                bitcrystalTradeAccount2Balance = clientJSON.getDouble("bitcrystalTradeAccount2Balance");
                DebugClient.println("caddaadadadool");
            } catch (JSONException ex) {
                bitcrystalTradeAccount2Balance = 0;
                DebugClient.println("cossssssol");
            }
        }
    }

    private synchronized void saveClient() {
        DebugClient.println("clientconnection@727");
        JSONObject json = server.loadJSON("client", "", "client.properties");
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
            json.put("txsendhashTradeAccountBitcoin", txsendhashTradeAccountBitcoin);
            json.put("txsendhashTradeAccountBitcrystal", txsendhashTradeAccountBitcrystal);
            json.put("txsendhashTradeAccount2Bitcoin", txsendhashTradeAccount2Bitcoin);
            json.put("txsendhashTradeAccount2Bitcrystal", txsendhashTradeAccount2Bitcrystal);
            json.put("bitcoinTradeAccountBalance", bitcoinTradeAccountBalance);
            json.put("bitcrystalTradeAccountBalance", bitcrystalTradeAccountBalance);
            json.put("bitcoinTradeAccount2Balance", bitcoinTradeAccount2Balance);
            json.put("bitcrystalTradeAccount2Balance", bitcrystalTradeAccount2Balance);
            server.saveJSON(json, "client", "", "client.properties");
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
            try {
                bitcrystalrpc.importPrivKey(privKey);
            } catch (Exception ex) {
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
        this.saveClient();
    }

    private void tradeabort() {
        if ((isEndedMe || isEndedOther || isSynced || isStarted) && isEnded == false) {
            this.server.close();
            setLastCommandStatus(true);
            return;
        }
        //String proofOfWorkSHA1 = HashFunctions.getProofOfWorkSHA1(1, currentTradePubKey);
        //System.out.println(proofOfWorkSHA1);
        currentTradeAddress = "";
        tradeWithAddress = "";
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
        this.server.send("tradeabort,," + temp);
        this.server.recvLight();
        this.server.close();
        isTradeAborted = true;
        setLastCommandStatus(true);
        return;
    }

    private void tradereset() {
        this.server.send("tradereset,,"+tradeAccount);
        this.server.send("tradereset,,"+tradeAccount);
        this.server.close();
        tradebtc2btcry = "";
        tradebtcry2btc = "";
        isEndedMe = false;
        isEndedOther = false;
        isSynced = false;
        isEnded = false;
        isStarted = false;
        isTradeAborted = true;
        setLastCommandStatus(true);
        this.saveClient();
        return;
    }

    private void starttrade() {
        DebugClient.println("@@@@clientconnection@175");
        if (!isSynced) {
            DebugClient.println("@@@@clientconnection@177");
            this.server.send("E_ERROR");
            this.server.close();
            setLastCommandStatus(false);
            return;
        }
        DebugClient.println("@@@@clientconnection@182");
        this.server.send("starttrade");
        DebugClient.println("clientconnection@184");
        String recv = this.server.recvLight();
        DebugClient.println("@@@@"+recv);
        DebugClient.println("@@@@clientconnection@187");
        if (recv.equals("E_ERROR")) {
            DebugClient.println("@@@@clientconnection@185");
            this.server.send("E_ERROR");
            this.server.close();
            setLastCommandStatus(false);
            return;
        }
        DebugClient.println("@@@@clientconnection@194");
        this.server.send(":)");
        this.server.close();
        isStarted = true;
        this.saveClient();
        setLastCommandStatus(true);
        return;
    }

    private void endtrademe() {
        try {
            DebugClient.println("@@@clientconnection@201");
            if (!isStarted) {
                DebugClient.println("@@@clientconnection@203");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            DebugClient.println("@@@clientconnection@208");
            this.server.send("endtrademe;" + tradeAccount);
            DebugClient.println("@@@clientconnection@210");
            String recv = this.server.recvLight();
            DebugClient.println("@@@"+recv);
            DebugClient.println("@@@clientconnection@213");
            if (recv.equals("E_ERROR")) {
                DebugClient.println("@@@clientconnection@215");
                this.server.sendLight("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (!tradebtc2btcry.isEmpty()) {
                DebugClient.println("@@@clientconnection@221");
                if (!recv.startsWith("btc2btcry;;")) {
                    DebugClient.println("@@@clientconnection@223");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("@@@clientconnection@228");
                if (!recv.contains(";;")) {
                    DebugClient.println("@@@clientconnection@229");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String[] split2 = recv.split(";;");
                DebugClient.println("@@@clientconnection@236");
                if (split2.length != 5) {
                    DebugClient.println("@@@clientconnection@238");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String myTradeAccount = split2[1];
                String myTradeWithAddress = split2[2];
                String myPrice = split2[3];
                String myTransaction = split2[4];
                if (!myTradeAccount.equals(tradeAccount)) {
                    DebugClient.println("@@@clientconnection@248");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (!myTradeWithAddress.equals(tradeWithAddress)) {
                    DebugClient.println("@@@clientconnection@254");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                double price = 0;
                try {
                    price = Double.parseDouble(myPrice);
                    if (price <= 0) {
                        DebugClient.println("@@@clientconnection@263");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    DebugClient.println("@@@clientconnection@267");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("@@@"+tradebtc2btcry);
                String[] split1 = tradebtc2btcry.split(",,");
                DebugClient.println("@@@"+split1[0]);
                DebugClient.println("@@@"+split1[1]);
                double thisprice = 0;
                try {
                    DebugClient.println("@@@clientconnection@275");
                    DebugClient.println("@@@"+split1[1]);
                    thisprice = Double.parseDouble(split1[1]);
                    if (thisprice <= 0 || price != thisprice) {
                        DebugClient.println("@@@clientconnection@278");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    DebugClient.println("@@@clientconnection@282");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }

                Object[] values = {tradeAccount, tradeWithAddress, price, 0.00000001, 1, txsendhashTradeAccountBitcoin};
                String createrawtransaction_multisig = bitcoinrpc.createrawtransaction_multisig(values);
                DebugClient.println("@@@"+myTransaction);
                DebugClient.println("@@@"+createrawtransaction_multisig);
                if (!bitcoinrpc.testtransactionequals_multisig(createrawtransaction_multisig, myTransaction)) {
                    DebugClient.println("@@@clientconnection@290");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("@@@clientconnection@295");
                JsonObject decodeRawTransactionMultisig = null;
                try {
                    DebugClient.println("@@@clientconnection@297");
                    decodeRawTransactionMultisig = bitcoinrpc.decodeRawTransactionMultisig(createrawtransaction_multisig);
                    DebugClient.println("@@@clientconnection@297 decodeRawTransactionMultisig - " + createrawtransaction_multisig);
                } catch (Exception ex2) {
                    DebugClient.println("@@@clientconnection@301");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String signrawtransaction_multisig = bitcoinrpc.signrawtransaction_multisig(createrawtransaction_multisig, 1);
                DebugClient.println("@@@clientconnection@297 signrawtransactionmultisig - "+signrawtransaction_multisig);
                this.server.sendLight(signrawtransaction_multisig);
                String recv1 = this.server.recvLight();
                if (recv1.equals("E_ERROR")) {
                    DebugClient.println("@@@clientconnection@310");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (recv1.equals("ALL_OK")) {
                    DebugClient.println("@@@clientconnection@316");
                    this.server.close();
                    setLastCommandStatus(true);
                    return;
                }
            } else if (!tradebtcry2btc.isEmpty()) {
                DebugClient.println("@@@clientconnection@321");
                if (!recv.startsWith("btcry2btc;;")) {
                    DebugClient.println("@@@clientconnection@323");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (!recv.contains(";;")) {
                    DebugClient.println("@@@clientconnection@329");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String[] split2 = recv.split(";;");
                DebugClient.println("@@@clientconnection@335");
                if (split2.length != 5) {
                    DebugClient.println("@@@clientconnection@337");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String myTradeAccount = split2[1];
                String myTradeWithAddress = split2[2];
                String myPrice = split2[3];
                String myTransaction = split2[4];
                if (!myTradeAccount.equals(tradeAccount)) {
                    DebugClient.println("@@@clientconnection@347");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (!myTradeWithAddress.equals(tradeWithAddress)) {
                    DebugClient.println("@@@clientconnection@353");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                double price = 0;
                try {
                    DebugClient.println("@@@clientconnection@360");
                    price = Double.parseDouble(myPrice);
                    if (price <= 0) {
                        DebugClient.println("@@@clientconnection@363");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    DebugClient.println("@@@clientconnection@367");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String[] split1 = tradebtcry2btc.split(",,");
                double thisprice = 0;
                try {
                    DebugClient.println("@@@clientconnection@375");
                    DebugClient.println(split1[1]);
                    thisprice = Double.parseDouble(split1[1]);
                    if (thisprice <= 0 || price != thisprice) {
                        DebugClient.println("@@@clientconnection@378");
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    DebugClient.println("@@@clientconnection@381");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                Object[] values = {tradeAccount, tradeWithAddress, price, 0.00000001, 1, txsendhashTradeAccountBitcrystal};
                String createrawtransaction_multisig = bitcrystalrpc.createrawtransaction_multisig(values);
                if (!bitcrystalrpc.testtransactionequals_multisig(createrawtransaction_multisig, myTransaction)) {
                    DebugClient.println("@@@clientconnection@390");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                JsonObject decodeRawTransactionMultisig = null;
                try {
                    DebugClient.println("@@@clientconnection@397");
                    decodeRawTransactionMultisig = bitcrystalrpc.decodeRawTransactionMultisig(createrawtransaction_multisig);
                    DebugClient.println("@@@clientconnection@397 decodeRawTransactionMultisig - " + createrawtransaction_multisig);
                } catch (Exception ex2) {
                    DebugClient.println("@@@clientconnection@400");
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String signrawtransaction_multisig = bitcrystalrpc.signrawtransaction_multisig(createrawtransaction_multisig, 1);
                DebugClient.println("@@@clientconnection@397 signrawtransaction_multisig - " + signrawtransaction_multisig);
                this.server.sendLight(signrawtransaction_multisig);
                String recv1 = this.server.recvLight();
                DebugClient.println("@@@clientconnection@408");
                if (recv1.equals("E_ERROR")) {
                    DebugClient.println("@@@clientconnection@410");
                    this.server.send("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                if (recv1.equals("ALL_OK")) {
                    DebugClient.println("@@@clientconnection@416");
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
            DebugClient.println("@@@clientconnection@424");
            if (!isStarted) {
                DebugClient.println("@@@clientconnection@362");
                this.server.send("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            DebugClient.println("@@@clientconnection@367");
            this.server.send("endtradeother;" + tradeAccount);
            DebugClient.println("@@@clientconnection@369");
            String recv = this.server.recvLight();
            DebugClient.println("@@@"+recv);
            DebugClient.println("@@@clientconnection@370");
            if (recv.equals("E_ERROR")) {
                DebugClient.println("@@@clientconnection@374");
                this.server.sendLight("E_ERROR");
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (!tradebtc2btcry.isEmpty()) {
                DebugClient.println("@@@clientconnection@380");
                String[] split1 = tradebtc2btcry.split(",,");
                JsonObject decodeRawTransactionMultisig1 = null;
                try {
                    DebugClient.println("@@@clientconnection@385");
                    DebugClient.println("@@@"+recv);
                    decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(recv);
                    DebugClient.println("@@@clientconnection@720");
                    DebugClient.println("@@@clientconnection@721");
                    if (!decodeRawTransactionMultisig1.has("complete")) {
                        DebugClient.println("@@@clientconnection@389");
                        this.server.sendLight("E_ERROR");
                        this.server.close();
                        setLastCommandStatus(false);
                        return;
                    }
                    DebugClient.println("@@@clientconnection@728");
                    String asString = decodeRawTransactionMultisig1.get("toaddress").getAsString();
                    double asDouble = decodeRawTransactionMultisig1.get("amount").getAsDouble();
                    String currencyprefix = decodeRawTransactionMultisig1.get("currencyprefix").getAsString();
                    DebugClient.println("@@@clientconnection@732");
                    if (!(("" + asDouble).equals(split1[0]))
                            || !currencyprefix.equals("BTCRY")
                            || !asString.equals(currentTradeAddress)) {
                        DebugClient.println("@@@clientconnection@398");
                        this.server.sendLight("E_ERROR");
                        this.server.close();
                        setLastCommandStatus(false);
                        return;
                    }
                } catch (Exception ex2) {
                    DebugClient.println("@@@clientconnection@404");
                    DebugClient.println("@@@clientconnection@404 - " + ex2.toString());
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                String signrawtransaction_multisig1 = bitcrystalrpc.signrawtransaction_multisig(recv, 1);
                try {
                    DebugClient.println("@@@clientconnection@411");
                    DebugClient.println("@@@"+signrawtransaction_multisig1);
                    decodeRawTransactionMultisig1 = bitcrystalrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                    JsonElement get = decodeRawTransactionMultisig1.get("complete");
                    if (get.getAsBoolean() == true) {
                        DebugClient.println("@@@clientconnection@415");
                        throw new Exception();
                    }
                } catch (Exception ex2) {
                    DebugClient.println("@@@clientconnection@419");
                    DebugClient.println("@@@clientconnection@419 - " + ex2.toString());
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("@@@clientconnection@424");
                DebugClient.println("@@@clientconnection@424 - " + signrawtransaction_multisig1);
                this.server.sendLight(signrawtransaction_multisig1);
                DebugClient.println("@@@clientconnection@426");
                this.server.recvLight();
                this.server.close();
                setLastCommandStatus(true);
                this.saveClient();
                return;
            } else if (!tradebtcry2btc.isEmpty()) {
                DebugClient.println("@@@clientconnection@430");
                DebugClient.println("@@@"+recv);
                DebugClient.println("@@@clientconnection@432");
                String[] split1 = tradebtcry2btc.split(",,");
                JsonObject decodeRawTransactionMultisig1 = null;
                try {
                    DebugClient.println("@@@clientconnection@437");
                    decodeRawTransactionMultisig1 = bitcoinrpc.decodeRawTransactionMultisig(recv);
                    if (!decodeRawTransactionMultisig1.has("complete")) {
                        DebugClient.println("@@@clientconnection@440");
                        this.server.sendLight("E_ERROR");
                        this.server.close();
                        setLastCommandStatus(false);
                        return;
                    }
                    String asString = decodeRawTransactionMultisig1.get("toaddress").getAsString();
                    double asDouble = decodeRawTransactionMultisig1.get("amount").getAsDouble();
                    String currencyprefix = decodeRawTransactionMultisig1.get("currencyprefix").getAsString();
                    if (!(("" + asDouble).equals(split1[0]))
                            || !currencyprefix.equals("BTC")
                            || !asString.equals(currentTradeAddress)) {
                        DebugClient.println("@@@clientconnection@449");
                        this.server.sendLight("E_ERROR");
                        this.server.close();
                        setLastCommandStatus(false);
                        return;
                    }
                } catch (Exception ex2) {
                    DebugClient.println("@@@clientconnection@455");
                    DebugClient.println("@@@clientconnection@455 - " + ex2.toString());
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("@@@clientconnection@460");
                String signrawtransaction_multisig1 = bitcoinrpc.signrawtransaction_multisig(recv);
                try {
                    DebugClient.println("@@@clientconnection@462");
                    decodeRawTransactionMultisig1 = bitcoinrpc.decodeRawTransactionMultisig(signrawtransaction_multisig1);
                    JsonElement get = decodeRawTransactionMultisig1.get("complete");
                    if (get.getAsBoolean() == true) {
                        DebugClient.println("@@@clientconnection@466");
                        throw new Exception();
                    }
                } catch (Exception ex2) {
                    DebugClient.println("@@@clientconnection@470");
                    DebugClient.println("@@@clientconnection@470 - " + ex2.toString());
                    this.server.sendLight("E_ERROR");
                    this.server.close();
                    setLastCommandStatus(false);
                    return;
                }
                DebugClient.println("@@@clientconnection@476");
                DebugClient.println("@@@clientconnection@476 - " + signrawtransaction_multisig1);
                this.server.sendLight(signrawtransaction_multisig1);
                DebugClient.println("@@@clientconnection@478");
                this.server.recvLight();
                DebugClient.println("@@@clientconnection@480");
                this.server.close();
                this.saveClient();
                setLastCommandStatus(true);
                isEndedOther = true;
                return;
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            DebugClient.println("@@@clientconnection@1095 - " + ex.toString());
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
            this.server.send("tradewith," + split[1] + "," + currentTradePubKey);
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
            if (split1.length != 5) {
                this.server.close();
                DebugClient.println("skjsfkljdskljfdkljdsikljsdfkljdkl");
                setLastCommandStatus(false);
                return;
            }

            tradeWithAddress = split1[4];
            DebugClient.println(tradeWithAddress);
            tradeAccount = currentTradeAddress + "," + tradeWithAddress;
            tradeAccount2 = tradeWithAddress + "," + currentTradeAddress;
            Object[] values1 = {split1[0], tradeAccount};
            Object[] values2 = {split1[1], tradeAccount};
            Object[] values3 = {split1[2], tradeAccount2};
            Object[] values4 = {split1[3], tradeAccount2};
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            try {
                DebugClient.println("dddddd1");
                bitcoinrpc.addmultisigaddressex(values1);
            } catch (Exception ex2) {
            }
            try {
                DebugClient.println("dddddd2");
                bitcrystalrpc.addmultisigaddressex(values2);
            } catch (Exception ex2) {
            }
            try {
                DebugClient.println("dddddd3");
                bitcoinrpc.addmultisigaddressex(values3);
            } catch (Exception ex2) {
            }
            try {
                DebugClient.println("dddddd4");
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
            createtrade();
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
            double balance = bitcrystalrpc.getbalancefrommultisigaddress_multisigex(tradeAccount);
            try {
                DebugClient.println("clientconnection@566");
                amount = Double.parseDouble(split[1]);
                price = Double.parseDouble(split[2]);
                if (amount <= 0 || price <= 0 || price + 0.00000001 > balance) {
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

    private void createtrade() {
        this.server.send("createtrade,," + tradeAccount);
        this.server.recvLight();
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
            createtrade();
            DebugClient.println("clientconnection@600");
            this.server.send("E_ERROR");
            this.server.close();
            double amount = 0;
            double price = 0;
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            double balance = bitcoinrpc.getbalancefrommultisigaddress_multisigex(tradeAccount);
            try {
                System.out.println("clientconnection@607");
                amount = Double.parseDouble(split[1]);
                price = Double.parseDouble(split[2]);
                if (amount <= 0 || price <= 0 || price + 0.00000001 > balance) {
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
            if (tradeAccount == null || tradeAccount.isEmpty()) {
                return "";
            }
            String string = bitcoinrpc.getmultisigaddressofaddressoraccount(tradeAccount);
            if (string == null || string.isEmpty() || string.equalsIgnoreCase("false")) {
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
            if (tradeAccount == null || tradeAccount.isEmpty()) {
                return "";
            }
            String string = bitcrystalrpc.getmultisigaddressofaddressoraccount(tradeAccount);
            if (string == null || string.isEmpty() || string.equalsIgnoreCase("false")) {
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
            if (string == null || string.isEmpty() || string.equalsIgnoreCase("false")) {
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
            if (string == null || string.isEmpty() || string.equalsIgnoreCase("false")) {
                return "";
            }
            tradeAccount2MultisigAddressBitcrystal = string;
            return string;
        } catch (Exception ex) {
            return "";
        }
    }

    @Deprecated
    public static double getBitcoinBalanceTradeAccount() {
        if (tradeAccountMultisigAddressBitcoin.isEmpty()) {
            return -1;
        }
        if (tradeAccountMultisigAddressBitcoin.startsWith("1")) {
            return -1;
        }
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            return bitcoinrpc.getBalance(tradeAccount);
        } catch (Exception ex) {
            return -1;
        }
    }

    @Deprecated
    public static double getBitcrystalBalanceTradeAccount() {
        if (tradeAccountMultisigAddressBitcrystal.isEmpty()) {
            return -1;
        }
        if (tradeAccountMultisigAddressBitcrystal.startsWith("1")) {
            return -1;
        }
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            return bitcrystalrpc.getBalance(tradeAccount);
        } catch (Exception ex) {
            return -1;
        }
    }

    @Deprecated
    public static double getBitcoinBalanceTradeAccount2() {
        if (tradeAccount2MultisigAddressBitcoin.isEmpty()) {
            return -1;
        }
        if (tradeAccount2MultisigAddressBitcoin.startsWith("1")) {
            return -1;
        }
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            return bitcoinrpc.getBalance(tradeAccount2);
        } catch (Exception ex) {
            return -1;
        }
    }

    @Deprecated
    public static double getBitcrystalBalanceTradeAccount2() {
        if (tradeAccount2MultisigAddressBitcrystal.isEmpty()) {
            return -1;
        }
        if (tradeAccount2MultisigAddressBitcrystal.startsWith("1")) {
            return -1;
        }
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            return bitcrystalrpc.getBalance(tradeAccount2);
        } catch (Exception ex) {
            return -1;
        }
    }

    public static void updateTxSendHash() {
        DecentralizedExchange.command("updatebalancetxsendhash");
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
        return tradeWithAddress;
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

    private void updatebalance() {
        DecentralizedExchange.command("updatebalancebitcoin");
        DecentralizedExchange.command("updatebalancebitcrystal");
        DecentralizedExchange.command("updatebalancebitcoin2");
        DecentralizedExchange.command("updatebalancebitcrystal2");
    }

    private void updatebalancebitcoin() {
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (tradeAccount == null || tradeAccount.isEmpty() || tradeAccount2 == null || tradeAccount2.isEmpty()) {
                this.server.close();
                DebugClient.println("@@ClientConnection 1484");
                setLastCommandStatus(false);
                return;
            }
            if (tradeAccountMultisigAddressBitcoin.isEmpty() || tradeAccountMultisigAddressBitcrystal.isEmpty() || tradeAccount2MultisigAddressBitcoin.isEmpty() || tradeAccount2MultisigAddressBitcrystal.isEmpty()) {
                this.server.close();
                DebugClient.println("@@ClientConnection 1491");
                setLastCommandStatus(false);
                return;
            }
            String sendedtxidsfrommultisigaddressex_multisigex = bitcoinrpc.getsendedtxidsfrommultisigaddressex_multisigex(tradeAccount);
            double balance_multisigex = bitcoinrpc.getbalancefrommultisigaddress_multisigex(tradeAccount);
            txsendhashTradeAccountBitcoin = sendedtxidsfrommultisigaddressex_multisigex;
            bitcoinTradeAccountBalance = balance_multisigex;
            this.server.send("updatebalancebitcoin____" + tradeAccount + "____" + txsendhashTradeAccountBitcoin + ",," + bitcoinTradeAccountBalance);
            this.server.recv();
            DebugClient.println("@@ClientConnection 1500 - " + "updatebalancebitcoin____" + tradeAccount + "____" + txsendhashTradeAccountBitcoin + ",," + bitcoinTradeAccountBalance);
            this.server.close();
            DebugClient.println("@@ClientConnection 1502");
            this.saveClient();
            setLastCommandStatus(true);
            return;
        } catch (Exception ex) {
            this.server.close();
            DebugClient.println("@@ClientConnection 1509");
            setLastCommandStatus(false);
            DebugClient.println("@@ClientConnection 1509 - " + ex.getMessage());
            return;
        }
    }

    private void updatebalancebitcrystal() {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            if (tradeAccount == null || tradeAccount.isEmpty() || tradeAccount2 == null || tradeAccount2.isEmpty()) {
                this.server.close();
                DebugClient.println("@@ClientConnection 1519");
                setLastCommandStatus(false);
                return;
            }
            if (tradeAccountMultisigAddressBitcoin.isEmpty() || tradeAccountMultisigAddressBitcrystal.isEmpty() || tradeAccount2MultisigAddressBitcoin.isEmpty() || tradeAccount2MultisigAddressBitcrystal.isEmpty()) {
                this.server.close();
                DebugClient.println("@@ClientConnection 1526");
                setLastCommandStatus(false);
                return;
            }
            String sendedtxidsfrommultisigaddressex_multisigex = bitcrystalrpc.getsendedtxidsfrommultisigaddressex_multisigex(tradeAccount);
            double balance_multisigex = bitcrystalrpc.getbalancefrommultisigaddress_multisigex(tradeAccount);
            txsendhashTradeAccountBitcrystal = sendedtxidsfrommultisigaddressex_multisigex;
            bitcrystalTradeAccountBalance = balance_multisigex;
            this.saveClient();
            this.server.send("updatebalancebitcrystal____" + tradeAccount + "____" + txsendhashTradeAccountBitcrystal + ",," + bitcrystalTradeAccountBalance);
            this.server.recv();
            DebugClient.println("@@ClientConnection 1535 - " + "updatebalancebitcrystal____" + tradeAccount + "____" + txsendhashTradeAccountBitcrystal + ",," + bitcrystalTradeAccountBalance);
            this.server.close();
            DebugClient.println("@@ClientConnection 1537");
            this.saveClient();
            setLastCommandStatus(true);
            return;
        } catch (Exception ex) {
            this.server.close();
            DebugClient.println("@@ClientConnection 1542");
            setLastCommandStatus(false);
            DebugClient.println("@@ClientConnection 1542 - " + ex.toString());
            return;
        }
    }

    private void updatebalancebitcoin2() {
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (tradeAccount == null || tradeAccount.isEmpty() || tradeAccount2 == null || tradeAccount2.isEmpty()) {
                this.server.close();
                DebugClient.println("@@ClientConnection 1553");
                setLastCommandStatus(false);
                return;
            }
            if (tradeAccountMultisigAddressBitcoin.isEmpty() || tradeAccountMultisigAddressBitcrystal.isEmpty() || tradeAccount2MultisigAddressBitcoin.isEmpty() || tradeAccount2MultisigAddressBitcrystal.isEmpty()) {
                this.server.close();
                DebugClient.println("@@ClientConnection 1559");
                setLastCommandStatus(false);
                return;
            }
            DebugClient.println("@@ClientConnection 1567");
            String sendedtxidsfrommultisigaddressex_multisigex = bitcoinrpc.getsendedtxidsfrommultisigaddressex_multisigex(tradeAccount2);
            DebugClient.println("@@ClientConnection 1568");
            double balance_multisigex = bitcoinrpc.getbalancefrommultisigaddress_multisigex(tradeAccount2);
            DebugClient.println("@@ClientConnection 1569");
            DebugClient.println("@@ClientConnection 1569 - " + "updatebalancebitcoin2____" + tradeAccount2 + "____" + sendedtxidsfrommultisigaddressex_multisigex + ",," + balance_multisigex);
            DebugClient.println("@@ClientConnection 1570");
            this.server.send("updatebalancebitcoin____" + tradeAccount2 + "____" + sendedtxidsfrommultisigaddressex_multisigex + ",," + balance_multisigex);
            DebugClient.println("@@ClientConnection 1571");
            this.server.recv();
            DebugClient.println("@@ClientConnection 1572");
            DebugClient.println("@@ClientConnection 1567 - " + "updatebalancebitcoin2____" + tradeAccount2 + "____" + sendedtxidsfrommultisigaddressex_multisigex + ",," + balance_multisigex);
            this.server.close();
            DebugClient.println("@@ClientConnection 1569");
            this.saveClient();
            setLastCommandStatus(true);
            return;
        } catch (Exception ex) {
            this.server.close();
            DebugClient.println("@@ClientConnection 1576");
            DebugClient.println("@@ClientConnection 1576 - " + ex.toString());
            setLastCommandStatus(false);
            return;
        }
    }

    private void updatebalancebitcrystal2() {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            if (tradeAccount == null || tradeAccount.isEmpty() || tradeAccount2 == null || tradeAccount2.isEmpty()) {
                this.server.close();
                DebugClient.println("@@ClientConnection 1586");
                setLastCommandStatus(false);
                return;
            }
            if (tradeAccountMultisigAddressBitcoin.isEmpty() || tradeAccountMultisigAddressBitcrystal.isEmpty() || tradeAccount2MultisigAddressBitcoin.isEmpty() || tradeAccount2MultisigAddressBitcrystal.isEmpty()) {
                this.server.close();
                DebugClient.println("@@ClientConnection 1592");
                setLastCommandStatus(false);
                return;
            }
            String sendedtxidsfrommultisigaddressex_multisigex = bitcrystalrpc.getsendedtxidsfrommultisigaddressex_multisigex(tradeAccount2);
            double balance_multisigex = bitcrystalrpc.getbalancefrommultisigaddress_multisigex(tradeAccount2);
            this.server.send("updatebalancebitcrystal____" + tradeAccount2 + "____" + sendedtxidsfrommultisigaddressex_multisigex + ",," + balance_multisigex);
            this.server.recv();
            DebugClient.println("@@ClientConnection 1599 - " + "updatebalancebitcrystal2____" + tradeAccount2 + "____" + sendedtxidsfrommultisigaddressex_multisigex + ",," + balance_multisigex);
            this.server.close();
            DebugClient.println("@@ClientConnection 1596");
            setLastCommandStatus(true);
            this.saveClient();
            return;
        } catch (Exception ex) {
            this.server.close();
            DebugClient.println("@@ClientConnection 1607");
            DebugClient.println("@@ClientConnection 1607 - " + ex.toString());
            setLastCommandStatus(false);
            return;
        }
    }

    private void getbalance() {
        DecentralizedExchange.command("getbalancebitcoin");
        DecentralizedExchange.command("getbalancebitcrystal");
    }

    private void getbalancebitcoin() {
        try {
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            if (tradeAccount == null || tradeAccount.isEmpty() || tradeAccount2 == null || tradeAccount2.isEmpty()) {
                this.server.close();
                setLastCommandStatus(false);
                DebugClient.println("@@ClientConnection 1632");
                return;
            }
            if (tradeAccountMultisigAddressBitcoin.isEmpty() || tradeAccountMultisigAddressBitcrystal.isEmpty() || tradeAccount2MultisigAddressBitcoin.isEmpty() || tradeAccount2MultisigAddressBitcrystal.isEmpty()) {
                this.server.close();
                setLastCommandStatus(false);
                DebugClient.println("@@ClientConnection 1638");
                return;
            }
            DebugClient.println("@@ClientConnection 1641" + "getbalancebitcoin____" + tradeAccount2);
            this.server.send("getbalancebitcoin____" + tradeAccount2);
            String recv = this.server.recv();
            if (recv.equalsIgnoreCase("E_ERROR")) {
                this.server.close();
                setLastCommandStatus(false);
                DebugClient.println("@@ClientConnection 1647");
                return;
            }
            if (!recv.contains(",,")) {
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            String[] split = recv.split(",,");
            if (split.length != 2) {
                this.server.close();
                setLastCommandStatus(false);
                DebugClient.println("@@ClientConnection 1659");
                return;
            }
            double tradeAccount2BitcoinBalance = 0;
            try {
                tradeAccount2BitcoinBalance = Double.parseDouble(split[1]);
                double balancefromtxids_multisigex = bitcoinrpc.getbalancefromtxids_multisigex(tradeAccount2, split[0]);
                if (tradeAccount2BitcoinBalance != balancefromtxids_multisigex || balancefromtxids_multisigex == 0) {
                    tradeAccount2BitcoinBalance = 0;
                    this.server.close();
                    setLastCommandStatus(false);
                    DebugClient.println("@@ClientConnection 1670");
                    return;
                }
                this.server.close();
                DebugClient.println("@@ClientConnection 1674");
                txsendhashTradeAccount2Bitcoin = split[0];
                bitcoinTradeAccount2Balance = tradeAccount2BitcoinBalance;
                this.saveClient();
                setLastCommandStatus(true);
                return;
            } catch (Exception ex) {
                DebugClient.println("@@ClientConnection 1682");
                DebugClient.println("@@ClientConnection 1682 - " + ex.toString());
                tradeAccount2BitcoinBalance = 0;
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
        } catch (Exception ex) {
            DebugClient.println("@@ClientConnection 1689");
            this.server.close();
            setLastCommandStatus(false);
            return;
        }
    }

    private void getbalancebitcrystal() {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            if (tradeAccount == null || tradeAccount.isEmpty() || tradeAccount2 == null || tradeAccount2.isEmpty()) {
                this.server.close();
                setLastCommandStatus(false);
                DebugClient.println("@@ClientConnection 1702");
                return;
            }
            if (tradeAccountMultisigAddressBitcoin.isEmpty() || tradeAccountMultisigAddressBitcrystal.isEmpty() || tradeAccount2MultisigAddressBitcoin.isEmpty() || tradeAccount2MultisigAddressBitcrystal.isEmpty()) {
                this.server.close();
                setLastCommandStatus(false);
                DebugClient.println("@@ClientConnection 1709");
                return;
            }
            DebugClient.println("@@ClientConnection 1702 " + "getbalancebitcrystal____" + tradeAccount2);
            this.server.send("getbalancebitcrystal____" + tradeAccount2);
            String recv = this.server.recv();
            if (recv.equalsIgnoreCase("E_ERROR")) {
                this.server.close();
                setLastCommandStatus(false);
                DebugClient.println("@@ClientConnection 1718");
                return;
            }
            if (!recv.contains(",,")) {
                this.server.close();
                setLastCommandStatus(false);
                return;
            }
            String[] split = recv.split(",,");
            if (split.length != 2) {
                this.server.close();
                setLastCommandStatus(false);
                DebugClient.println("@@ClientConnection 1730");
                return;
            }
            double tradeAccount2BitcrystalBalance = 0;
            try {
                tradeAccount2BitcrystalBalance = Double.parseDouble(split[1]);
                double balancefromtxids_multisigex1 = bitcrystalrpc.getbalancefromtxids_multisigex(tradeAccount2, split[0]);
                if (tradeAccount2BitcrystalBalance != balancefromtxids_multisigex1 || balancefromtxids_multisigex1 == 0) {
                    tradeAccount2BitcrystalBalance = 0;
                    this.server.close();
                    setLastCommandStatus(false);
                    DebugClient.println("@@ClientConnection 1740");
                    return;
                }
                this.server.close();
                DebugClient.println("@@ClientConnection 1744");
                txsendhashTradeAccount2Bitcrystal = split[0];
                bitcrystalTradeAccount2Balance = tradeAccount2BitcrystalBalance;
                this.saveClient();
                setLastCommandStatus(true);
                return;
            } catch (Exception ex) {
                DebugClient.println("@@ClientConnection 1751");
                tradeAccount2BitcrystalBalance = 0;
                this.server.close();
                DebugClient.println("@@ClientConnection 1754 - " + ex.getMessage());
                setLastCommandStatus(false);
                return;
            }
        } catch (Exception ex) {
            DebugClient.println("@@ClientConnection 1759");
            this.server.close();
            setLastCommandStatus(false);
            return;
        }
    }

    public static double getBitcoinBalanceTradeAccountEx() {
        return bitcoinTradeAccountBalance;
    }

    public static double getBitcoinBalanceTradeAccount2Ex() {
        return bitcoinTradeAccount2Balance;
    }

    public static double getBitcrystalBalanceTradeAccountEx() {
        return bitcrystalTradeAccountBalance;
    }

    public static double getBitcrystalBalanceTradeAccount2Ex() {
        return bitcrystalTradeAccount2Balance;
    }

    public static String getBitcoinTxSendHashTradeAccountEx() {
        return txsendhashTradeAccountBitcoin;
    }

    public static String getBitcoinTxSendHashTradeAccount2Ex() {
        return txsendhashTradeAccount2Bitcoin;
    }

    public static String getBitcrystalTxSendHashTradeAccountEx() {
        return txsendhashTradeAccountBitcrystal;
    }

    public static String getBitcrystalTxSendHashTradeAccount2Ex() {
        return txsendhashTradeAccount2Bitcrystal;
    }
}
