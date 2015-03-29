package de.bitcrystal.decentralizedexchange;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ConcurrentConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class DecentralizedExchange {

    private static ConcurrentConfig config;
    private static File configuration;
    private static List<String> nodeservers;
    private static TCPServer tcpServer;
    private static int nodeServerPort;
    private static int nodeClientPort;
    private static String password;
    private static String salt;
    private static String publickey;
    private static String privatekey;
    
    public static void start()
    {
        connection();
        tcpServer = new TCPServer(nodeClientPort);
        tcpServer.start();
    }

    public static String getStringOutCommand(String[] args) {
        if (args == null || args.length == 0) {
            return "";
        }

        String argsd = args[0];
        int argslength = args.length;
        for (int i = 1; i < argslength; i++) {
            argsd = argsd + " " + args[i];
        }
        return argsd;
    }

    public static void main(String[] args) {
        DecentralizedExchange.start();
        command(args);
    }
    
    public static void command(final String command) {
        if (command == null || command.isEmpty()) {
            return;
        }
        new Thread(new Runnable() {

            public void run() {
                List<String> nodeServers = getNodeServers();
                int length = nodeServers.size();
                int port = nodeServerPort;
                for (int i = 0; i < length; i++) {
                    boolean serverConnection = serverConnection(nodeServers.get(i), port, command);
                    if(serverConnection)
                        break;
                }
            }
        }).start();
    }

    public static void command(String[] args) {
        String command = getStringOutCommand(args);
        command(command);
    }

    public static void connection() {
        configuration = new File("node.properties");
        if (!configuration.exists()) {
            config = new ConcurrentConfig(configuration);
            Map<String, String> con = new ConcurrentHashMap<String, String>();
            con.put("nodeserver1", "176.57.142.30");
            con.put("nodeserver2", "NOTHING");
            con.put("nodeserver3", "NOTHING");
            con.put("nodeserver4", "NOTHING");
            con.put("nodeserver5", "NOTHING");
            con.put("nodeserver6", "NOTHING");
            con.put("nodeserver7", "NOTHING");
            con.put("nodeserver8", "NOTHING");
            con.put("nodeserver9", "NOTHING");
            con.put("nodeserver10", "NOTHING");
            con.put("nodeserverport", "6789");
            con.put("nodeclientport", "6789");
            con.put("publickey", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJBdtKSNQo6azZqmUn2oQ1oEIn8z8m9mI1e4ZGPwhNzGXwklOV26lgMvtnd7exMxj1X60PMpYlGJDqPWaQ40cw7tLsnapbQiS4WpoG0Et+HL2a8aYLoTToj5l+PHA4bVdGkwJyItnGcWwX7B2JlPDJjHZwis79mZFCNXQHe+0VKwIDAQAB");
            con.put("privatekey", "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMkF20pI1CjprNmqZSfahDWgQifzPyb2YjV7hkY/CE3MZfCSU5XbqWAy+2d3t7EzGPVfrQ8yliUYkOo9ZpDjRzDu0uydqltCJLhamgbQS34cvZrxpguhNOiPmX48cDhtV0aTAnIi2cZxbBfsHYmU8MmMdnCKzv2ZkUI1dAd77RUrAgMBAAECgYBrNLNhZ3u2IgDryGLQIUpW6xO9CI3KcqWnjivq9JyyGOrYpMDs78vhoO7QnFkbqHuMCK1bqIfIWtSWB47WgE8rSAk8ceJul15LUfWyh65dnQ7aJ+rqox2EVK2t496FenoICABn/KbmaPQN/r3LpgAZ+ZOMZTiOQjNI0OuFVUB1SQJBAPIPjJRktLQqKP1EaP3Ggqj2PS7EeHtO7HnBxUF/hI/+wIrjKHwPEUkRikDhmJszuy/r3lG/Y6NI+PTSAFEzYmUCQQDUmVQpiZ5tAweLNTlKTkXpEg9y7uqB8drB2iKw+VmvVi0Nj6dPXBAMhj6hmpnOxGrdjynz0opgkJYonQBi01hPAkEAz+ZK96kHCzaqvdxj0JMO5c+X/PMCB+ZhdLHYmcjMMmC7Po6b1vGaBwfplpAsYiCsRRxwdgXLrhKewKcdXqCjgQJAPv6L1J8FhXGfW51Ss3TL/EqwrzKh5A0g783N97h63ZxgTyNgxQAXdU6V4lan+n9y1uGj4a5h+Ej/ZVtYEPBkuwJBAIfxAXpcLBgVfzB06DDkHv1xzJmEdVDXWLaBjPhu6RqRXwf+fR3uozKFqHo76+ieeNSzHfnNj5kE47YCzAj5aYU=");
            con.put("password", "standard");
            con.put("salt", "cool");
            config.update(con);
            config.save("=");
        }
        Map<String, String> con = new ConcurrentHashMap<String, String>();
        config = new ConcurrentConfig(configuration);
        config.load(configuration, "=");
        con.putAll(config.getCopyOfProperties());
        Set<String> keys = new HashSet<String>();
        keys.addAll(con.keySet());
        nodeservers = new CopyOnWriteArrayList<String>();
        String t = "";
        for (String key : keys) {
            t = con.get(key);
            if (key.equalsIgnoreCase("nodeserverport")) {
                try {
                    nodeServerPort = Integer.parseInt(t);
                } catch (Exception ex) {
                    nodeServerPort = 6789;
                }
                continue;
            }
            if (key.equalsIgnoreCase("nodeclientport")) {
                try {
                    nodeClientPort = Integer.parseInt(t);
                } catch (Exception ex) {
                    nodeClientPort = 6789;
                }
                continue;
            }
            if (key.equalsIgnoreCase("publickey")) {
                    publickey = t;
                continue;
            }
            if (key.equalsIgnoreCase("privatekey")) {
                    privatekey = t;
                continue;
            }
            if (key.equalsIgnoreCase("password")) {
                    password = t;
                continue;
            }
            if (key.equalsIgnoreCase("salt")) {
                salt = t;
                continue;
            }
            if (!key.startsWith("nodeserver")) {
                continue;
            }
            if (t == null || t.contains("NOTHING")
                    || t.equalsIgnoreCase("NOTHING") || !t.contains(".") || t.equalsIgnoreCase("127.0.0.1") || t.equalsIgnoreCase("localhost")) {
                continue;
            }
            nodeservers.add(t);
        }
    }

    public static List<String> getNodeServers() {
        return nodeservers;
    }

    public static int getNodeServerPort() {
        return nodeServerPort;
    }

    private static void serverConnection(final TCPClient tcpClient, final String command) {
        new Thread(new Runnable() {

            public void run() {
                new Thread(new ClientConnection(tcpClient, command)).start();
            }
        }).start();
    }
    private static boolean serverConnection(final String host, final int port, String command) {
        TCPClient tcpClient = new TCPClient(host, port);
        if (!tcpClient.isValidConnection()) {
            return false;
        }
        serverConnection(tcpClient, command);
        return true;
    }
    
    public static TCPClientSecurity getSecurityClient(TCPClient tcpClient)
    {
        return new TCPClientSecurity(tcpClient, publickey, privatekey, password, salt);
    }
}
