/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
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

/**
 *
 * @author ABC
 */
public class TCPClientSecurity {

    private TCPClient tcpClient;
    private String pubkey;
    private String privkey;
    private String password;
    private String salt;
    private static KeyPairGenerator keygen=null;

    public TCPClientSecurity(int port)
    {
        this.tcpClient=new TCPClient(port);
        init();
    }
    
    public TCPClientSecurity(String host, int port)
    {
        this.tcpClient=new TCPClient(host, port);
        init();
    }
    
    public TCPClientSecurity(String host)
    {
        this.tcpClient=new TCPClient(host);
        init();
    }
    
    public TCPClientSecurity(Socket socket)
    {
        this.tcpClient=new TCPClient(socket);
        init();
    }
    
    public TCPClientSecurity(TCPClient tcpClient)
    {
        this.tcpClient=tcpClient;
        init();
    }
    
    public TCPClientSecurity(int port, String password, String salt) {
        this(port);
        this.password=password;
        this.salt=salt;
    }
    
     public TCPClientSecurity(String host, int port, String password, String salt) {
        this(host, port);
        this.password=password;
        this.salt=salt;
    }
     
      public TCPClientSecurity(String host, String password, String salt) {
        this(host);
        this.password=password;
        this.salt=salt;
    }
      
      public TCPClientSecurity(Socket socket, String password, String salt) {
          this(socket);
          this.password=password;
          this.salt=salt;
      }
      
      public TCPClientSecurity(TCPClient tcpClient, String password, String salt) {
          this.tcpClient=tcpClient;
          this.password=password;
          this.salt=salt;
      }
              
    
    public TCPClientSecurity(int port, String pubkey, String privkey, String password, String salt) {
        this(port, password, salt);
        this.pubkey=pubkey;
        this.privkey=privkey;
    }

    public TCPClientSecurity(String host, int port, String pubkey, String privkey, String password, String salt) {
        this(host, port, password, salt);
        this.pubkey=pubkey;
        this.privkey=privkey;
    }

    public TCPClientSecurity(String host, String pubkey, String privkey, String password, String salt) {
       this(host,password,salt);
       this.pubkey=pubkey;
       this.privkey=privkey;
    }

    public TCPClientSecurity(Socket socket, String pubkey, String privkey, String password, String salt) {
        this(socket,password,salt);
        this.pubkey=pubkey;
        this.privkey=privkey;
    }

    public TCPClientSecurity(TCPClient tcpClient, String pubkey, String privkey, String password, String salt) {
        this(tcpClient,password,salt);
        this.pubkey = pubkey;
        this.privkey = privkey;
    }

    public TCPClient getTCPClient() {
        return this.tcpClient;
    }

    public void send(String string) {
        String encryptAES256 = HashFunctions.encryptAES256(string, password, salt);
        String encryptRSA = HashFunctions.encryptRSA(encryptAES256, pubkey);
        this.tcpClient.send(encryptRSA);
    }

    public String recv() {
        String recv = this.tcpClient.recv();
        String decryptRSA = HashFunctions.decryptRSA(recv, privkey);
        String decryptAES256 = HashFunctions.decryptAES256(decryptRSA, password, salt);
        return decryptAES256;
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
    
    public DataOutputStream getOutputStream() {
        return this.tcpClient.getOutputStream();
    }

    public BufferedReader getInputStream() {
        return this.tcpClient.getInputStream();
    }
    
    private void init()
    {
        this.pubkey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJBdtKSNQo6azZqmUn2oQ1oEIn8z8m9mI1e4ZGPwhNzGXwklOV26lgMvtnd7exMxj1X60PMpYlGJDqPWaQ40cw7tLsnapbQiS4WpoG0Et+HL2a8aYLoTToj5l+PHA4bVdGkwJyItnGcWwX7B2JlPDJjHZwis79mZFCNXQHe+0VKwIDAQAB";
        this.privkey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMkF20pI1CjprNmqZSfahDWgQifzPyb2YjV7hkY/CE3MZfCSU5XbqWAy+2d3t7EzGPVfrQ8yliUYkOo9ZpDjRzDu0uydqltCJLhamgbQS34cvZrxpguhNOiPmX48cDhtV0aTAnIi2cZxbBfsHYmU8MmMdnCKzv2ZkUI1dAd77RUrAgMBAAECgYBrNLNhZ3u2IgDryGLQIUpW6xO9CI3KcqWnjivq9JyyGOrYpMDs78vhoO7QnFkbqHuMCK1bqIfIWtSWB47WgE8rSAk8ceJul15LUfWyh65dnQ7aJ+rqox2EVK2t496FenoICABn/KbmaPQN/r3LpgAZ+ZOMZTiOQjNI0OuFVUB1SQJBAPIPjJRktLQqKP1EaP3Ggqj2PS7EeHtO7HnBxUF/hI/+wIrjKHwPEUkRikDhmJszuy/r3lG/Y6NI+PTSAFEzYmUCQQDUmVQpiZ5tAweLNTlKTkXpEg9y7uqB8drB2iKw+VmvVi0Nj6dPXBAMhj6hmpnOxGrdjynz0opgkJYonQBi01hPAkEAz+ZK96kHCzaqvdxj0JMO5c+X/PMCB+ZhdLHYmcjMMmC7Po6b1vGaBwfplpAsYiCsRRxwdgXLrhKewKcdXqCjgQJAPv6L1J8FhXGfW51Ss3TL/EqwrzKh5A0g783N97h63ZxgTyNgxQAXdU6V4lan+n9y1uGj4a5h+Ej/ZVtYEPBkuwJBAIfxAXpcLBgVfzB06DDkHv1xzJmEdVDXWLaBjPhu6RqRXwf+fR3uozKFqHo76+ieeNSzHfnNj5kE47YCzAj5aYU=";
        this.password="standard";
        this.salt="cool";
    }
    
    public String getPublicKey()
    {
        return pubkey;
    }
    
    public String getPrivateKey()
    {
        return privkey;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public String getSalt()
    {
        return salt;
    }
    
    public static Map<String,String> getKeyPair()
    {
        Map<String,String> output = new ConcurrentHashMap<String, String>();
         String pubKeyToString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJBdtKSNQo6azZqmUn2oQ1oEIn8z8m9mI1e4ZGPwhNzGXwklOV26lgMvtnd7exMxj1X60PMpYlGJDqPWaQ40cw7tLsnapbQiS4WpoG0Et+HL2a8aYLoTToj5l+PHA4bVdGkwJyItnGcWwX7B2JlPDJjHZwis79mZFCNXQHe+0VKwIDAQAB";
        String privKeyToString = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMkF20pI1CjprNmqZSfahDWgQifzPyb2YjV7hkY/CE3MZfCSU5XbqWAy+2d3t7EzGPVfrQ8yliUYkOo9ZpDjRzDu0uydqltCJLhamgbQS34cvZrxpguhNOiPmX48cDhtV0aTAnIi2cZxbBfsHYmU8MmMdnCKzv2ZkUI1dAd77RUrAgMBAAECgYBrNLNhZ3u2IgDryGLQIUpW6xO9CI3KcqWnjivq9JyyGOrYpMDs78vhoO7QnFkbqHuMCK1bqIfIWtSWB47WgE8rSAk8ceJul15LUfWyh65dnQ7aJ+rqox2EVK2t496FenoICABn/KbmaPQN/r3LpgAZ+ZOMZTiOQjNI0OuFVUB1SQJBAPIPjJRktLQqKP1EaP3Ggqj2PS7EeHtO7HnBxUF/hI/+wIrjKHwPEUkRikDhmJszuy/r3lG/Y6NI+PTSAFEzYmUCQQDUmVQpiZ5tAweLNTlKTkXpEg9y7uqB8drB2iKw+VmvVi0Nj6dPXBAMhj6hmpnOxGrdjynz0opgkJYonQBi01hPAkEAz+ZK96kHCzaqvdxj0JMO5c+X/PMCB+ZhdLHYmcjMMmC7Po6b1vGaBwfplpAsYiCsRRxwdgXLrhKewKcdXqCjgQJAPv6L1J8FhXGfW51Ss3TL/EqwrzKh5A0g783N97h63ZxgTyNgxQAXdU6V4lan+n9y1uGj4a5h+Ej/ZVtYEPBkuwJBAIfxAXpcLBgVfzB06DDkHv1xzJmEdVDXWLaBjPhu6RqRXwf+fR3uozKFqHo76+ieeNSzHfnNj5kE47YCzAj5aYU=";
        try {
            if(keygen==null)
                keygen=HashFunctions.getDefaultKeygenGenerator();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TCPClientSecurity.class.getName()).log(Level.SEVERE, null, ex);
              output.put("publickey", pubKeyToString);
              output.put("privatekey", privKeyToString);
              return output;
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(TCPClientSecurity.class.getName()).log(Level.SEVERE, null, ex);
            output.put("publickey", pubKeyToString);
            output.put("privatekey", privKeyToString);
            return output;
        }
        KeyPair genKeyPair = keygen.genKeyPair();
        pubKeyToString = HashFunctions.pubKeyToString(genKeyPair.getPublic());
        privKeyToString = HashFunctions.privKeyToString(genKeyPair.getPrivate());
        output.put("publickey", pubKeyToString);
        output.put("privatekey", privKeyToString);
        return output;
    }
}
