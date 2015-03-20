/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import de.bitcrystal.decentralizedexchange.security.BitCrystalKeyGenerator;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class ServerConnection implements Runnable {

    private static Map<String, String> traders = new ConcurrentHashMap<String, String>();
    private static Map<String, String> traderspw = new ConcurrentHashMap<String, String>();
    private static Map<String, Map<String, String>> users = new ConcurrentHashMap<String, Map<String, String>>();
    private TCPClient client;

    public ServerConnection(TCPClient client) {
        this.client = client;
    }

    public void run() {
        try {
            String recv = client.recv();
            if (recv.equals("0")) {
                sniffersex();
                return;
            }
            String hostAddress = client.getSocket().getInetAddress().getHostAddress();
            if (!users.containsKey(hostAddress)) {
                client.close();
                return;
            }
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            String decodeDataSecurityEmail = bitcrystalrpc.decodeDataSecurityEmail(recv);
            String decodeDataSecurityEmailHash = bitcrystalrpc.decodeDataSecurityEmailHash(decodeDataSecurityEmail);
            if (!decodeDataSecurityEmailHash.contains(",")) {
                this.client.close();
                return;
            }
            String[] split = decodeDataSecurityEmailHash.split(",");
            if (split.length < 1) {
                this.client.close();
                return;
            }
            switch (split.length) {
                case 5: {
                    if (split[0].equalsIgnoreCase("register")) {
                        if (traders.containsKey(split[3])) {
                            this.client.close();
                            return;
                        }
                        String bitcoinAddressOfPubKey = bitcrystalrpc.getBitcoinAddressOfPubKey(split[2]);
                        if (!split[1].equals(bitcoinAddressOfPubKey)) {
                            this.client.close();
                            return;
                        }
                        traders.put(split[3], split[1]);
                        traderspw.put(split[3], split[4]);
                    }
                }
                break;
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sniffersex() {
        final String hostAddress = client.getSocket().getInetAddress().getHostAddress();
        this.client.send("SNIFFER_IS_FUCKED");
        this.client.close();
        new Thread(new Runnable() {

            public void run() {
                Map<String, String> map = null;
                if (users.containsKey(hostAddress)) {
                    map = users.get(hostAddress);
                    String get1 = map.get("currentPasswdIterations");
                    String get2 = map.get("byteSizeHash");
                    String get3 = map.get("byteSizeKey");
                    int cpi = 0;
                    int bsh = 0;
                    int bsk = 0;
                    try {
                        cpi = Integer.parseInt(get1);
                    } catch (Throwable ex) {
                    }
                    try {
                        bsh = Integer.parseInt(get2);
                    } catch (Throwable ex) {
                    }
                    try {
                        bsk = Integer.parseInt(get3);
                    } catch (Throwable ex) {
                    }
                    map.put("currentPasswdIterations", "" + BitCrystalKeyGenerator.getPasswordIterations(cpi));
                    map.put("byteSizeHash", "" + BitCrystalKeyGenerator.getByteSizeHash(bsh));
                    map.put("byteSizeKey", "" + BitCrystalKeyGenerator.getByteSizeKey(bsk));
                    users.put(hostAddress, map);
                    return;
                }
                map = new ConcurrentHashMap<String, String>();
                String sha256 = "";
                while (true) {
                    sha256 = HashFunctions.sha256(HashFunctions.sha256(hostAddress));
                    if (sha256.startsWith("000000")) {
                        break;
                    }
                }
                map.put("iphash", sha256);
                map.put("currentPasswdIterations", "65534");
                map.put("byteSizeHash", "499");
                map.put("byteSizeKey", "999");
                users.put(hostAddress, map);
            }
        }).start();
        return;
    }
}
