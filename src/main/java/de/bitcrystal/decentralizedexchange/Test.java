/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import de.bitcrystal.decentralizedexchange.security.HashFunctions;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author ABC
 */
public class Test {
    public static void main(String[] args) {
        try {
            KeyPairGenerator defaultKeygenGenerator = HashFunctions.getDefaultKeygenGenerator();
            KeyPair genKeyPair = defaultKeygenGenerator.genKeyPair();
            String pubkey = HashFunctions.pubKeyToString(genKeyPair.getPublic());
            String privkey = HashFunctions.privKeyToString(genKeyPair.getPrivate());
            System.out.println(pubkey);
            System.out.println(privkey);
            String hallo = "hallo";
            String encryptRSA = HashFunctions.encryptRSA(hallo, pubkey);
            String decrypt = HashFunctions.decryptRSA(encryptRSA, privkey);
            try
            {
                System.out.println(decrypt);
            } catch (Exception ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
