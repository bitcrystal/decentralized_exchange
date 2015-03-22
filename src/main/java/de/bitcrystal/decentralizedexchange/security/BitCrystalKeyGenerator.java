/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange.security;

import java.security.SecureRandom;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class BitCrystalKeyGenerator {

    private long timestampHash = 1426866844;
    private long timestampKey = 1426867073;
    private boolean isInit = false;
    private SecureRandom currentHashSecureRandom;
    private SecureRandom currentKeySecureRandom;
    private int currentPasswdIterations;
    private int byteSizeHash;
    private int byteSizeKey;
    private String byteHash;
    private String byteKey;
    private static BitCrystalKeyGenerator INSTANCE=null;

    public BitCrystalKeyGenerator() {
        init();
    }

    private void init() {
        if (isInit) {
            return;
        }
        currentHashSecureRandom = new SecureRandom();
        currentKeySecureRandom = new SecureRandom();
        currentHashSecureRandom.setSeed(timestampHash);
        currentKeySecureRandom.setSeed(timestampKey);
        currentPasswdIterations = 65534;
        byteSizeHash = 499;
        byteSizeKey = 999;
        byteHash = generateNewHash();
        byteKey = generateNewKey();
    }

    private String generateNewHash() {
        return generateNewHash(byteSizeHash, currentHashSecureRandom);
    }

    private String generateNewKey() {
        return generateNewKey(byteSizeKey, currentKeySecureRandom);
    }

    public static String generateNewHash(int byteSize, SecureRandom secureRandom) {
        String hash = HashFunctions.generateSalt(byteSize, secureRandom);
        hash = HashFunctions.sha384(hash);
        hash = HashFunctions.sha256(hash);
        hash = HashFunctions.sha512(hash);
        hash = HashFunctions.sha512(hash);
        hash = HashFunctions.sha256(hash);
        hash = HashFunctions.sha384(hash);
        hash = HashFunctions.sha512(hash);
        return hash;
    }

    public static String generateNewKey(int byteSize, SecureRandom secureRandom) {
        String hash = HashFunctions.generateSalt(byteSize, secureRandom);
        hash = HashFunctions.sha512(hash);
        hash = HashFunctions.sha256(hash);
        hash = HashFunctions.sha512(hash);
        hash = HashFunctions.sha512(hash);
        hash = HashFunctions.sha256(hash);
        hash = HashFunctions.sha384(hash);
        hash = HashFunctions.sha512(hash);
        return hash;
    }

    public String encrypt(String encrypt) {
        try {
            String enc = HashFunctions.encryptAES256(encrypt, byteHash, currentPasswdIterations, byteKey);
            update();
            return enc;
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public String decrypt(String decrypt) {
        try {
            String dec = HashFunctions.decryptAES256(decrypt, byteHash, currentPasswdIterations, byteKey);
            update();
            return dec;
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public static String encrypt(String encrypt, int byteSizeHash, int byteSizeKey, int currentPasswdIterations, SecureRandom currentHashSecureRandom, SecureRandom currentKeySecureRandom, String sniffersex) {
        try {
            return HashFunctions.encryptAES256(encrypt, HashFunctions.sha512(generateNewHash(byteSizeHash, currentHashSecureRandom) + sniffersex), getPasswordIterations(currentPasswdIterations), HashFunctions.sha512(generateNewKey(byteSizeKey, currentKeySecureRandom) + sniffersex));
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public static String decrypt(String decrypt, int byteSizeHash, int byteSizeKey, int currentPasswdIterations, SecureRandom currentHashSecureRandom, SecureRandom currentKeySecureRandom, String sniffersex) {
        try {
            return HashFunctions.decryptAES256(decrypt, HashFunctions.sha512(generateNewHash(byteSizeHash, currentHashSecureRandom) + sniffersex), getPasswordIterations(currentPasswdIterations), HashFunctions.sha512(generateNewKey(byteSizeKey, currentKeySecureRandom) + sniffersex));
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public int getByteSizeHash() {
        return getByteSizeHash(byteSizeHash);
    }

    public int getByteSizeKey() {
        return getByteSizeKey(byteSizeKey);
    }

    public int getPasswordIterations() {
        return getPasswordIterations(currentPasswdIterations);
    }

    private void update() {
        try{
            currentPasswdIterations = getPasswordIterations();
            byteSizeHash = getByteSizeHash();
            byteSizeKey = getByteSizeKey();
            byteHash=generateNewHash();
            byteKey=generateNewKey();
        } catch(Exception ex)
        {
            isInit=false;
            init();
        }
    }

    public static int getByteSizeHash(int byteSizeHash) {
        byteSizeHash++;
        if (byteSizeHash < 500 || byteSizeHash >= 1000) {
            byteSizeHash = 500;
        }
        return byteSizeHash;
    }

    public static int getByteSizeKey(int byteSizeKey) {
        byteSizeKey++;
        if (byteSizeKey < 1000 || byteSizeKey >= 1500) {
            byteSizeKey = 1000;
        }
        return byteSizeKey;
    }

    public static int getPasswordIterations(int currentPasswdIterations) {
        currentPasswdIterations++;
        if (currentPasswdIterations < 65535 || currentPasswdIterations >= 100000) {
            currentPasswdIterations = 65535;
        }
        return currentPasswdIterations;
    }
    
    public static BitCrystalKeyGenerator getInstance()
    {
        if(INSTANCE==null)
        {
            INSTANCE=new BitCrystalKeyGenerator();
        }
        return INSTANCE;
    }
}
