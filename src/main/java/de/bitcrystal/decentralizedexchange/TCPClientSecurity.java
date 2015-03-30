/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.google.common.collect.BiMap;
import de.bitcrystal.decentralizedexchange.security.BitCrystalJSON;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ABC
 */
public class TCPClientSecurity {

    private TCPClient tcpClient;
    private String password;
    private String salt;

    public TCPClientSecurity(int port) {
        this.tcpClient = new TCPClient(port);
        init();
    }

    public TCPClientSecurity(String host, int port) {
        this.tcpClient = new TCPClient(host, port);
        init();
    }

    public TCPClientSecurity(String host) {
        this.tcpClient = new TCPClient(host);
        init();
    }

    public TCPClientSecurity(Socket socket) {
        this.tcpClient = new TCPClient(socket);
        init();
    }

    public TCPClientSecurity(TCPClient tcpClient) {
        this.tcpClient = tcpClient;
        init();
    }

    public TCPClientSecurity(int port, String password, String salt) {
        this(port);
        this.password = password;
        this.salt = salt;
    }

    public TCPClientSecurity(String host, int port, String password, String salt) {
        this(host, port);
        this.password = password;
        this.salt = salt;
    }

    public TCPClientSecurity(String host, String password, String salt) {
        this(host);
        this.password = password;
        this.salt = salt;
    }

    public TCPClientSecurity(Socket socket, String password, String salt) {
        this(socket);
        this.password = password;
        this.salt = salt;
    }

    public TCPClientSecurity(TCPClient tcpClient, String password, String salt) {
        this.tcpClient = tcpClient;
        this.password = password;
        this.salt = salt;
    }

    public TCPClient getTCPClient() {
        return this.tcpClient;
    }

    public void send(String string) {
        String encryptAES256 = HashFunctions.encryptAES256(string, password, salt);
        this.tcpClient.send(encryptAES256);
    }

    public String recv() {
        String recv = this.tcpClient.recv();
        String decryptAES256 = HashFunctions.decryptAES256(recv, password, salt);
        return decryptAES256;
    }

    public void sendSecurity(String string) {
        String encodeString = BitCrystalJSON.encodeString(string);
        String encryptAES256 = HashFunctions.encryptAES256(encodeString, password, salt);
        this.tcpClient.send(encryptAES256);
    }

    public String recvSecurity() {
        String recv = this.tcpClient.recv();
        String decryptAES256 = HashFunctions.decryptAES256(recv, password, salt);
        decryptAES256 = BitCrystalJSON.decodeString(decryptAES256);
        return decryptAES256;
    }

    public void sendSecurityWallet(String string) {
        String encodeString = BitCrystalJSON.encodeWalletString(string);
        String encryptAES256 = HashFunctions.encryptAES256(encodeString, password, salt);
        this.tcpClient.send(encryptAES256);
    }

    public String recvSecurityWallet() {
        String recv = this.tcpClient.recv();
        String decryptAES256 = HashFunctions.decryptAES256(recv, password, salt);
        decryptAES256 = BitCrystalJSON.decodeWalletString(decryptAES256);
        return decryptAES256;
    }

    public void sendJSONObject(JSONObject jsonObject) {
        String string = BitCrystalJSON.encode(jsonObject);
        String encryptAES256 = HashFunctions.encryptAES256(string, password, salt);
        this.tcpClient.send(encryptAES256);
    }

    public JSONObject recvJSONObject() {
        String recv = this.tcpClient.recv();
        String decryptAES256 = HashFunctions.decryptAES256(recv, password, salt);
        JSONObject jsonObject = BitCrystalJSON.decode(decryptAES256);
        return jsonObject;
    }

    public void sendJSONObjectWallet(JSONObject jsonObject) {
        String string = BitCrystalJSON.encodeWallet(jsonObject);
        String encryptAES256 = HashFunctions.encryptAES256(string, password, salt);
        this.tcpClient.send(encryptAES256);
    }

    public JSONObject recvJSONObjectWallet() {
        String recv = this.tcpClient.recv();
        String decryptAES256 = HashFunctions.decryptAES256(recv, password, salt);
        JSONObject jsonObject = BitCrystalJSON.decodeWallet(decryptAES256);
        return jsonObject;
    }

    public void close() {
        this.tcpClient.close();
    }

    public Socket getSocket() {
        return this.tcpClient.getSocket();
    }

    public String getHostAddress() {
        return this.tcpClient.getHostAddress();
    }

    public boolean isValidConnection() {
        return this.tcpClient.isValidConnection();
    }

    public OutputStream getOutputStream() {
        return this.tcpClient.getOutputStream();
    }

    public InputStream getInputStream() {
        return this.tcpClient.getInputStream();
    }

    private void init() {
        this.password = "standard";
        this.salt = "cool";
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean saveString(String string, String key, String path) {
        return saveString(string, key, path, "");
    }

    public boolean saveString(String stringk, String key, String path, String filename) {
        String string = BitCrystalJSON.encodeString(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        String encryptAES256 = HashFunctions.encryptAES256(string, password, salt);
        return BitCrystalJSON.saveString(encryptAES256, key, path, filename);
    }

    public String loadString(String key, String path) {
        return loadString(key, path, "");
    }

    public String loadString(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        String decryptAES256 = HashFunctions.decryptAES256(string, password, salt);
        return BitCrystalJSON.decodeString(decryptAES256);
    }
    
    public boolean saveStringWallet(String string, String key, String path) {
        return saveStringWallet(string, key, path, "");
    }

    public boolean saveStringWallet(String stringk, String key, String path, String filename) {
        String string = BitCrystalJSON.encodeWalletString(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        String encryptAES256 = HashFunctions.encryptAES256(string, password, salt);
        return BitCrystalJSON.saveString(encryptAES256, key, path, filename);
    }

    public String loadStringWallet(String key, String path) {
        return loadStringWallet(key, path, "");
    }

    public String loadStringWallet(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        String decryptAES256 = HashFunctions.decryptAES256(string, password, salt);
        return BitCrystalJSON.decodeWalletString(decryptAES256);
    }

    public boolean saveJSONObject(JSONObject jsonObject, String key, String path) {
        return saveJSONObject(jsonObject, key, path, "");
    }

    public boolean saveJSONObject(JSONObject jsonObject, String key, String path, String filename) {
        return saveString(jsonObject.toString(), key, path, filename);
    }

    public JSONObject loadJSONObject(String key, String path) {
        return loadJSONObject(key, path, "");
    }

    public JSONObject loadJSONObject(String key, String path, String filename) {
        try {
            String string = loadString(key, path, filename);
            if (string == null || string.isEmpty()) {
                return null;
            }
            JSONObject jSONObject = new JSONObject(string);
            return jSONObject;
        } catch (JSONException ex) {
            return null;
        }

    }

    public boolean saveJSONObjectWallet(JSONObject jsonObject, String key, String path) {
        return saveJSONObjectWallet(jsonObject, key, path, "");
    }

    public boolean saveJSONObjectWallet(JSONObject jsonObject, String key, String path, String filename) {
        return saveStringWallet(jsonObject.toString(), key, path, filename);
    }

    public JSONObject loadJSONObjectWallet(String key, String path) {
       return loadJSONObjectWallet(key, path, "");
    }

    public JSONObject loadJSONObjectWallet(String key, String path, String filename) {
        String string = loadStringWallet(key, path, filename);
        JSONObject jSONObject = null;
        try {
            jSONObject = new JSONObject(string);
            return jSONObject;
        } catch (JSONException ex) {
            return null;
        }
    }
}
