package de.bitcrystal.decentralizedexchange;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import de.bitcrystal.decentralizedexchange.security.BitCrystalKeyGenerator;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ConcurrentConfig;
import java.io.File;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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
    private static int commandExecutorPort;
    private static String password;
    private static String salt;
    private static String publickey;
    private static String privatekey;
    private static boolean isStarted = false;
    private static KeyPairGenerator keygen;

    public static void start() {
        if (isStarted) {
            return;
        }
        isStarted = true;
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
        if(args==null||args.length==0)
        {
            DecentralizedExchangeGUI.main(args);
        } else {
            DecentralizedExchange.start();
            if(!(args.length==1&&args[0].equalsIgnoreCase("server")))
                command(args);
        }
    }

    public static void command(final String command) {
        if (command == null || command.isEmpty()) {
            return;
        }
        //new Thread(new Runnable() {

         //   public void run() {
                List<String> nodeServers = getNodeServers();
                int length = nodeServers.size();
                int port = nodeServerPort;
                for (int i = 0; i < length; i++) {
                    boolean serverConnection = serverConnection(nodeServers.get(i), port, command);
                    if (serverConnection) {
                        break;
                    }
                }
           // }
     //   }).start();
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
            con.put("nodeserverport", "6789");
            con.put("nodeclientport", "6789");
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
            if (key.equalsIgnoreCase("commandexecutorport")) {
                try {
                    commandExecutorPort = Integer.parseInt(t);
                } catch (Exception ex) {
                    commandExecutorPort = 3329;
                }
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
                    || t.equalsIgnoreCase("NOTHING")) {
                //!t.contains(".") || t.equalsIgnoreCase("127.0.0.1") || t.equalsIgnoreCase("localhost")
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

    public static int getNodeClientPort() {
        return nodeClientPort;
    }

    public static int getCommandExecutorPort() {
        return commandExecutorPort;
    }

    private static void serverConnection(final TCPClient tcpClient, final String command) {
       // new Thread(new Runnable() {
           // public void run() {
                //new Thread(new ClientConnection(tcpClient, command)).start();  
            //}
        //}).start();
        new ClientConnection(tcpClient, command).run();
    }

    private static boolean serverConnection(final String host, final int port, String command) {
        TCPClient tcpClient = new TCPClient(host, port);
        if (!tcpClient.isValidConnection()) {
            return false;
        }
        serverConnection(tcpClient, command);
        return true;
    }

    public static TCPClientSecurity getSecurityClient(TCPClient tcpClient) {
        if (!isStarted) {
            start();
        }
        return new TCPClientSecurity(tcpClient, password, salt);
    }

    public static RPCApp getBitcoinRPC() {
        try {
            RPCApp rpcApp = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            return rpcApp;
        } catch (Exception ex) {
            return null;
        }
    }

    public static RPCApp getBitcrystalRPC() {
        try {
            RPCApp rpcApp = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            return rpcApp;
        } catch (Exception ex) {
            return null;
        }
    }
}
