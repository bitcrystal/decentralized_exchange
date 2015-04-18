/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import com.google.common.collect.BiMap;
import de.bitcrystal.decentralizedexchange.security.BitCrystalJSON;
import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import de.bitcrystal.decentralizedexchange.security.Json;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
    private int packetLengthCool = -1;
    private int packetLengthCoolLight = -1;

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

    private String encryptedString(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String encryptAES256 = HashFunctions.encryptAES256(string, password, salt);
        return encryptAES256;
    }

    private String decryptedString(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String decryptAES256 = HashFunctions.decryptAES256(string, password, salt);
        return decryptAES256;
    }

    private String encodeString(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String encodeString = BitCrystalJSON.encodeString(string);
        String encryptAES256 = encryptedString(encodeString);
        return encryptAES256;
    }

    private String decodeString(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String decodeString = decryptedString(string);
        String decryptAES256 = BitCrystalJSON.decodeString(decodeString);
        return decryptAES256;
    }

    private String encodeStringLight(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String encodeString = BitCrystalJSON.encodeStringLight(string);
        String encryptAES256 = encryptedString(encodeString);
        return encryptAES256;
    }

    private String decodeStringLight(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String decodeString = decryptedString(string);
        String decryptAES256 = BitCrystalJSON.decodeStringLight(decodeString);
        return decryptAES256;
    }

    private String encodeStringCool(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (BitCrystalJSON.isNormalCool()) {
            return encodeString(string);
        } else if (BitCrystalJSON.isFastCool()) {
            return encodeStringLight(string);
        } else if (BitCrystalJSON.isFastestCool()) {
            return string;
        } else {
            return string;
        }
    }

    private String decodeStringCool(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (BitCrystalJSON.isNormalCool()) {
            return decodeString(string);
        } else if (BitCrystalJSON.isFastCool()) {
            return decodeStringLight(string);
        } else if (BitCrystalJSON.isFastestCool()) {
            return string;
        } else {
            return string;
        }
    }

    private String encodeStringCoolLight(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (BitCrystalJSON.isNormalCool() || BitCrystalJSON.isFastCool()) {
            return encodeStringLight(string);
        } else if (BitCrystalJSON.isFastestCool()) {
            return string;
        } else {
            return string;
        }
    }

    private String decodeStringCoolLight(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (BitCrystalJSON.isNormalCool() || BitCrystalJSON.isFastCool()) {
            return decodeStringLight(string);
        } else if (BitCrystalJSON.isFastestCool()) {
            return string;
        } else {
            return string;
        }
    }

    private String encodeWalletString(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String encodeString = BitCrystalJSON.encodeWalletString(string);
        String encryptAES256 = encryptedString(encodeString);
        return encryptAES256;
    }

    private String decodeWalletString(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String decodeString = decryptedString(string);
        String decryptAES256 = BitCrystalJSON.decodeWalletString(decodeString);
        return decryptAES256;
    }

    private String encodeWalletStringLight(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String encodeString = BitCrystalJSON.encodeWalletStringLight(string);
        String encryptAES256 = encryptedString(encodeString);
        return encryptAES256;
    }

    private String decodeWalletStringLight(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String decodeString = decryptedString(string);
        String decryptAES256 = BitCrystalJSON.decodeWalletStringLight(decodeString);
        return decryptAES256;
    }

    private String encodeWalletStringCool(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (BitCrystalJSON.isFastestCool()) {
            return string;
        } else if (BitCrystalJSON.isFastCool()) {
            return encodeWalletStringLight(string);
        } else {
            return encodeWalletString(string);
        }
    }

    private String decodeWalletStringCool(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (BitCrystalJSON.isFastestCool()) {
            return string;
        } else if (BitCrystalJSON.isFastCool()) {
            return decodeWalletStringLight(string);
        } else {
            return decodeWalletString(string);
        }
    }

    private String encodeWalletStringCoolLight(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (BitCrystalJSON.isNormalCool() || BitCrystalJSON.isFastCool()) {
            return encodeWalletStringLight(string);
        } else if (BitCrystalJSON.isFastestCool()) {
            return string;
        } else {
            return string;
        }
    }

    private String decodeWalletStringCoolLight(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (string == null || string.isEmpty()) {
            return "";
        }
        if (BitCrystalJSON.isNormalCool() || BitCrystalJSON.isFastCool()) {
            return decodeWalletStringLight(string);
        } else if (BitCrystalJSON.isFastestCool()) {
            return string;
        } else {
            return string;
        }
    }

    private String encode(JSONObject obj) {
        if (obj == null) {
            return "";
        }
        return encodeString(obj.toString());
    }

    private JSONObject decode(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(decodeString(string));
        } catch (JSONException ex) {
            return null;
        }
    }

    private String encodeLight(JSONObject obj) {
        if (obj == null) {
            return "";
        }
        return encodeStringLight(obj.toString());
    }

    private JSONObject decodeLight(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(decodeStringLight(string));
        } catch (JSONException ex) {
            return null;
        }
    }

    private String encodeCool(JSONObject obj) {
        if (obj == null) {
            return "";
        }
        return encodeStringCool(obj.toString());
    }

    private JSONObject decodeCool(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(decodeStringCool(string));
        } catch (JSONException ex) {
            return null;
        }
    }

    private String encodeCoolLight(JSONObject obj) {
        if (obj == null) {
            return "";
        }
        return encodeStringCoolLight(obj.toString());
    }

    private JSONObject decodeCoolLight(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(decodeStringCoolLight(string));
        } catch (JSONException ex) {
            return null;
        }
    }

    private String encodeWallet(JSONObject obj) {
        if (obj == null) {
            return "";
        }
        return encodeWalletString(obj.toString());
    }

    private JSONObject decodeWallet(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(decodeWalletString(string));
        } catch (JSONException ex) {
            return null;
        }
    }

    private String encodeWalletLight(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        return encodeWalletStringLight(obj.toString());
    }

    private JSONObject decodeWalletLight(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(decodeWalletStringLight(string));
        } catch (JSONException ex) {
            return null;
        }
    }

    private String encodeWalletCool(JSONObject obj) {
        if (obj == null) {
            return "";
        }
        return encodeWalletStringCool(obj.toString());
    }

    private JSONObject decodeWalletCool(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(decodeWalletStringCool(string));
        } catch (JSONException ex) {
            return null;
        }
    }

    private String encodeWalletCoolLight(JSONObject obj) {
        if (obj == null) {
            return "";
        }
        return encodeWalletStringCoolLight(obj.toString());
    }

    private JSONObject decodeWalletCoolLight(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        try {
            return new JSONObject(decodeWalletStringCoolLight(string));
        } catch (JSONException ex) {
            return null;
        }
    }

    public void send(String string) {
        this.sendSecurityCool(string);
    }

    public String recv() {
        String recv = this.recvSecurityCool();
        return recv;
    }

    public void sendLight(String string) {
        this.sendSecurityCoolLight(string);
    }

    public String recvLight() {
        String recv = this.recvSecurityCoolLight();
        return recv;
    }

    public void send(String data, int buffer) {
        this.tcpClient.send(encryptedString(data), buffer);
    }

    public String recv(int buffer) {
        String recv = this.tcpClient.recv(buffer);
        if (recv == null) {
            return "";
        }
        return decryptedString(recv);
    }

    public void sendSecurity(String string) {
        this.tcpClient.send(encodeString(string), 40000);
    }

    public String recvSecurity() {
        String recv = this.tcpClient.recv(40000);
        if (recv == null) {
            return "";
        }

        return decodeString(recv);
    }

    public void sendSecurityLight(String string) {
        this.tcpClient.send(encodeStringLight(string), 40000);
    }

    public String recvSecurityLight() {
        String recv = this.tcpClient.recv(500);
        if (recv == null) {
            return "";
        }

        return decodeStringLight(recv);
    }

    public void sendSecurityCool(String string) {
        this.tcpClient.send(encodeStringCool(string), getPacketLengthCool());
    }

    public String recvSecurityCool() {
        String recv = this.tcpClient.recv(getPacketLengthCool());
        if (recv == null) {
            return "";
        }
        return decodeStringCool(recv);
    }

    public void sendSecurityCoolLight(String string) {
        this.tcpClient.send(encodeStringCoolLight(string), getPacketLengthCoolLight());
    }

    public String recvSecurityCoolLight() {
        String recv = this.tcpClient.recv(getPacketLengthCoolLight());
        if (recv == null) {
            return "";
        }
        return decodeStringCoolLight(recv);
    }

    public void sendSecurityWallet(String string) {
        this.tcpClient.send(encodeWalletString(string), 40000);
    }

    public String recvSecurityWallet() {
        String recv = this.tcpClient.recv(40000);
        if (recv == null) {
            return "";
        }

        return decodeWalletString(recv);
    }

    public void sendSecurityWalletLight(String string) {
        this.tcpClient.send(encodeWalletStringLight(string), 40000);
    }

    public String recvSecurityWalletLight() {
        String recv = this.tcpClient.recv(40000);
        if (recv == null) {
            return "";
        }
        return decodeWalletStringLight(recv);
    }

    public void sendSecurityWalletCool(String string) {
        this.tcpClient.send(encodeWalletStringCool(string), getPacketLengthCool());
    }

    public String recvSecurityWalletCool() {
        String recv = this.tcpClient.recv(getPacketLengthCool());
        if (recv == null) {
            return "";
        }
        return decodeWalletStringCool(recv);
    }

    public void sendSecurityWalletCoolLight(String string) {
        this.tcpClient.send(encodeWalletStringCoolLight(string), getPacketLengthCoolLight());
    }

    public String recvSecurityWalletCoolLight() {
        String recv = this.tcpClient.recv(getPacketLengthCoolLight());
        if (recv == null) {
            return "";
        }
        return decodeWalletStringCoolLight(recv);
    }

    public void sendJSONObject(JSONObject jsonObject) {

        this.tcpClient.send(encode(jsonObject), 40000);
    }

    public JSONObject recvJSONObject() {

        String string = this.tcpClient.recv(40000);
        if (string == null) {
            return null;
        }

        return decode(string);
    }

    public void sendJSONObjectWallet(JSONObject jsonObject) {
        this.tcpClient.send(encodeWallet(jsonObject), 40000);
    }

    public JSONObject recvJSONObjectWallet() {
        String recv = this.tcpClient.recv(40000);
        if (recv == null) {
            return null;
        }

        return decodeWallet(recv);
    }

    public void sendJSONObjectLight(JSONObject jsonObject) {


        this.tcpClient.send(encodeLight(jsonObject), 40000);
    }

    public JSONObject recvJSONObjectLight() {

        String string = this.tcpClient.recv(40000);
        if (string == null) {
            return null;
        }
        return decodeLight(string);
    }

    public void sendJSONObjectWalletLight(JSONObject jsonObject) {
        this.tcpClient.send(encodeWalletLight(jsonObject), 40000);
    }

    public JSONObject recvJSONObjectWalletLight() {
        String recv = this.tcpClient.recv(40000);
        if (recv == null) {
            return null;
        }

        return decodeWalletLight(recv);
    }

    public void sendJSONObjectCool(JSONObject jsonObject) {

        this.tcpClient.send(encodeCool(jsonObject), getPacketLengthCool());
    }

    public JSONObject recvJSONObjectCool() {

        String string = this.tcpClient.recv(getPacketLengthCool());
        if (string == null) {
            return null;
        }

        return decodeCool(string);
    }

    public void sendJSONObjectWalletCool(JSONObject jsonObject) {

        this.tcpClient.send(encodeWalletCool(jsonObject), getPacketLengthCool());
    }

    public JSONObject recvJSONObjectWalletCool() {
        String recv = this.tcpClient.recv(getPacketLengthCool());
        if (recv == null) {
            return null;
        }
        return decodeWalletCool(recv);
    }

    public void sendJSONObjectCoolLight(JSONObject jsonObject) {

        this.tcpClient.send(encodeCoolLight(jsonObject), getPacketLengthCoolLight());
    }

    public JSONObject recvJSONObjectCoolLight() {

        String string = this.tcpClient.recv(getPacketLengthCoolLight());
        if (string == null) {
            return null;
        }

        return decodeCoolLight(string);
    }

    public void sendJSONObjectWalletCoolLight(JSONObject jsonObject) {

        this.tcpClient.send(encodeWalletCoolLight(jsonObject), getPacketLengthCoolLight());
    }

    public JSONObject recvJSONObjectWalletCoolLight() {
        String recv = this.tcpClient.recv(getPacketLengthCoolLight());
        if (recv == null) {
            return null;
        }
        return decodeWalletCoolLight(recv);
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

    public BitcrystalOutputStream getBitcrystalOutputStream() {
        return this.tcpClient.getBitcrystalOutputStream();
    }

    public BitcrystalInputStream getBitcrystalInputStream() {
        return this.tcpClient.getBitcrystalInputStream();
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
        String string = encodeString(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        return BitCrystalJSON.saveString(string, key, path, filename);
    }

    public String loadString(String key, String path) {
        return loadString(key, path, "");
    }

    public String loadString(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        return decodeString(string);
    }

    public boolean saveStringLight(String string, String key, String path) {
        return saveStringLight(string, key, path, "");
    }

    public boolean saveStringLight(String stringk, String key, String path, String filename) {
        String string = encodeStringLight(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        return BitCrystalJSON.saveString(string, key, path, filename);
    }

    public String loadStringLight(String key, String path) {
        return loadStringLight(key, path, "");
    }

    public String loadStringLight(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        return decodeStringLight(string);
    }

    public boolean saveStringCool(String string, String key, String path) {
        return saveStringCool(string, key, path, "");
    }

    public boolean saveStringCool(String stringk, String key, String path, String filename) {
        String string = encodeStringCool(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        return BitCrystalJSON.saveString(string, key, path, filename);
    }

    public String loadStringCool(String key, String path) {
        return loadString(key, path, "");
    }

    public String loadStringCool(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        return decodeStringCool(string);
    }

    public boolean saveStringCoolLight(String string, String key, String path) {
        return saveStringCoolLight(string, key, path, "");
    }

    public boolean saveStringCoolLight(String stringk, String key, String path, String filename) {
        String string = encodeStringCoolLight(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        return BitCrystalJSON.saveString(string, key, path, filename);
    }

    public String loadStringCoolLight(String key, String path) {
        return loadString(key, path, "");
    }

    public String loadStringCoolLight(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        return decodeStringCoolLight(string);
    }

    public boolean saveStringWallet(String string, String key, String path) {
        return saveStringWallet(string, key, path, "");
    }

    public boolean saveStringWallet(String stringk, String key, String path, String filename) {
        String string = encodeWalletString(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        return BitCrystalJSON.saveString(string, key, path, filename);
    }

    public String loadStringWallet(String key, String path) {
        return loadStringWallet(key, path, "");
    }

    public String loadStringWallet(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        return decodeWalletString(string);
    }

    public boolean saveStringWalletLight(String string, String key, String path) {
        return saveStringWalletLight(string, key, path, "");
    }

    public boolean saveStringWalletLight(String stringk, String key, String path, String filename) {
        String string = encodeWalletStringLight(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        return BitCrystalJSON.saveString(string, key, path, filename);
    }

    public String loadStringWalletLight(String key, String path) {
        return loadStringWalletLight(key, path, "");
    }

    public String loadStringWalletLight(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        return decodeWalletStringLight(string);
    }

    public boolean saveStringWalletCool(String string, String key, String path) {
        return saveStringWalletCool(string, key, path, "");
    }

    public boolean saveStringWalletCool(String stringk, String key, String path, String filename) {
        String string = encodeWalletStringCool(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        return BitCrystalJSON.saveString(string, key, path, filename);
    }

    public String loadStringWalletCool(String key, String path) {
        return loadStringWalletCool(key, path, "");
    }

    public String loadStringWalletCool(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        return decodeWalletStringCool(string);
    }

    public boolean saveStringWalletCoolLight(String string, String key, String path) {
        return saveStringWalletCoolLight(string, key, path, "");
    }

    public boolean saveStringWalletCoolLight(String stringk, String key, String path, String filename) {
        String string = encodeWalletStringCoolLight(stringk);
        if (string == null || string.isEmpty()) {
            return false;
        }
        return BitCrystalJSON.saveString(string, key, path, filename);
    }

    public String loadStringWalletCoolLight(String key, String path) {
        return loadStringWalletCoolLight(key, path, "");
    }

    public String loadStringWalletCoolLight(String key, String path, String filename) {
        String string = BitCrystalJSON.loadString(key, path, filename);
        if (string == null || string.isEmpty()) {
            return null;
        }
        return decodeWalletStringCoolLight(string);
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
        String string = loadString(key, path, filename);
        return Json.toJSONObject(string);
    }

    public boolean saveJSONObjectLight(JSONObject jsonObject, String key, String path) {
        return saveJSONObjectLight(jsonObject, key, path, "");
    }

    public boolean saveJSONObjectLight(JSONObject jsonObject, String key, String path, String filename) {
        return saveStringLight(jsonObject.toString(), key, path, filename);
    }

    public JSONObject loadJSONObjectLight(String key, String path) {
        return loadJSONObjectLight(key, path, "");
    }

    public JSONObject loadJSONObjectLight(String key, String path, String filename) {
        String string = loadStringLight(key, path, filename);
        return Json.toJSONObject(string);
    }

    public boolean saveJSONObjectCool(JSONObject jsonObject, String key, String path) {
        return saveJSONObjectCool(jsonObject, key, path, "");
    }

    public boolean saveJSONObjectCool(JSONObject jsonObject, String key, String path, String filename) {
        return saveStringCool(jsonObject.toString(), key, path, filename);
    }

    public JSONObject loadJSONObjectCool(String key, String path) {
        return loadJSONObjectCool(key, path, "");
    }

    public JSONObject loadJSONObjectCool(String key, String path, String filename) {
        String string = loadStringCool(key, path, filename);
        return Json.toJSONObject(string);
    }

    public boolean saveJSONObjectCoolLight(JSONObject jsonObject, String key, String path) {
        return saveJSONObjectCoolLight(jsonObject, key, path, "");
    }

    public boolean saveJSONObjectCoolLight(JSONObject jsonObject, String key, String path, String filename) {
        return saveStringCoolLight(jsonObject.toString(), key, path, filename);
    }

    public JSONObject loadJSONObjectCoolLight(String key, String path) {
        return loadJSONObjectCoolLight(key, path, "");
    }

    public JSONObject loadJSONObjectCoolLight(String key, String path, String filename) {
        String string = loadStringCoolLight(key, path, filename);
        return Json.toJSONObject(string);
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
        return Json.toJSONObject(string);
    }

    public boolean saveJSONObjectWalletLight(JSONObject jsonObject, String key, String path) {
        return saveJSONObjectWalletLight(jsonObject, key, path, "");
    }

    public boolean saveJSONObjectWalletLight(JSONObject jsonObject, String key, String path, String filename) {
        return saveStringWalletLight(jsonObject.toString(), key, path, filename);
    }

    public JSONObject loadJSONObjectWalletLight(String key, String path) {
        return loadJSONObjectWalletLight(key, path, "");
    }

    public JSONObject loadJSONObjectWalletLight(String key, String path, String filename) {
        String string = loadStringWalletLight(key, path, filename);
        return Json.toJSONObject(string);
    }

    public boolean saveJSONObjectWalletCool(JSONObject jsonObject, String key, String path) {
        return saveJSONObjectWalletCool(jsonObject, key, path, "");
    }

    public boolean saveJSONObjectWalletCool(JSONObject jsonObject, String key, String path, String filename) {
        return saveStringWalletCool(jsonObject.toString(), key, path, filename);
    }

    public JSONObject loadJSONObjectWalletCool(String key, String path) {
        return loadJSONObjectWalletCool(key, path, "");
    }

    public JSONObject loadJSONObjectWalletCool(String key, String path, String filename) {
        String string = loadStringWalletCool(key, path, filename);
        return Json.toJSONObject(string);
    }

    public boolean saveJSONObjectWalletCoolLight(JSONObject jsonObject, String key, String path) {
        return saveJSONObjectWalletCoolLight(jsonObject, key, path, "");
    }

    public boolean saveJSONObjectWalletCoolLight(JSONObject jsonObject, String key, String path, String filename) {
        return saveStringWalletCoolLight(jsonObject.toString(), key, path, filename);
    }

    public JSONObject loadJSONObjectWalletCoolLight(String key, String path) {
        return loadJSONObjectWalletCoolLight(key, path, "");
    }

    public JSONObject loadJSONObjectWalletCoolLight(String key, String path, String filename) {
        String string = loadStringWalletCoolLight(key, path, filename);
        return Json.toJSONObject(string);
    }

    public boolean saveJSON(JSONObject jsonObject, String key, String path) {
        return saveJSON(jsonObject, key, path, "");
    }

    public JSONObject loadJSON(String key, String path) {
        return loadJSON(key, path, "");
    }

    public boolean saveJSON(JSONObject jsonObject, String key, String path, String filename) {
        return save(jsonObject.toString(), key, path, filename);
    }

    public JSONObject loadJSON(String key, String path, String filename) {
        String string = load(key, path, filename);
        return Json.toJSONObject(string);
    }

    public boolean save(String string, String key, String path) {
        return save(string, key, path, "");
    }

    public String load(String key, String path) {
        return load(key, path, "");
    }

    public boolean save(String string, String key, String path, String filename) {
        return saveStringCool(string, key, path, filename);
    }

    public String load(String key, String path, String filename) {
        return loadStringCool(key, path, filename);
    }

    public int getPacketLengthCool() {
        if (packetLengthCool == -1) {
            if (BitCrystalJSON.isNormalCool()) {
                packetLengthCool = 300;
            } else if (BitCrystalJSON.isFastCool()) {
                packetLengthCool = 300;
            } else if (BitCrystalJSON.isFastestCool()) {
                packetLengthCool = 300;
            } else {
                packetLengthCool = 300;
            }
        }
        return packetLengthCool;
    }

    public int getPacketLengthCoolLight() {
        if (packetLengthCoolLight == -1) {
            if (BitCrystalJSON.isNormalCool() || BitCrystalJSON.isFastCool()) {
                packetLengthCoolLight = 300;
            } else if (BitCrystalJSON.isFastestCool()) {
                packetLengthCoolLight = 300;
            } else {
                packetLengthCoolLight = 300;
            }
        }
        return packetLengthCoolLight;
    }
}
