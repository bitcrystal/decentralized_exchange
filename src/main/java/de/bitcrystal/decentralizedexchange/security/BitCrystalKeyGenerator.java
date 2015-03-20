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
    private static long timestampHash = 1426866844;
    private static long timestampKey = 1426867073;
    private static String firstTzp = "FUCK THE SNIFFER IN THE ASS!";
    private static String secondTzp = "REALLY!";
    private static String currentHash="";
    private static String lastHash="";
    private static String currentKey="";
    private static String lastKey="";
    private static boolean isInit=false;
    private static SecureRandom currentHashSecureRandom;
    private static SecureRandom currentKeySecureRandom;
    private static int currentPasswdIterations;
    private static Map<String,Object> encrypted;
    private static int byteSizeHash;
    private static int byteSizeKey;
    
    
    private void init()
    {
        if(isInit)
            return;
        currentHashSecureRandom=new SecureRandom();
        currentKeySecureRandom=new SecureRandom();
        currentHashSecureRandom.setSeed(timestampHash);
        currentKeySecureRandom.setSeed(timestampKey);
        encrypted=null;
        currentPasswdIterations=65534;
        byteSizeHash=499;
        byteSizeKey=999;
    }
    
    public static String generateNewHash()
    {
        return generateNewHash(byteSizeHash, currentHashSecureRandom);
    }
    
    public static String generateNewKey()
    {
        return generateNewKey(byteSizeKey, currentKeySecureRandom);
    }
    
    public static String generateNewHash(int byteSize, SecureRandom secureRandom)
    {
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
    
    public static String generateNewKey(int byteSize, SecureRandom secureRandom)
    {
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
    
    public static String encrypt(String encrypt)
    {
        try {
            return HashFunctions.encryptAES256(encrypt, generateNewHash(), getPasswordIterations(), generateNewKey());
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static String decrypt(String decrypt)
    {
         try {
            return HashFunctions.decryptAES256(decrypt, generateNewHash(), getPasswordIterations(), generateNewKey());
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
     public static String encrypt(String encrypt, int byteSizeHash, int byteSizeKey, int currentPasswdIterations, SecureRandom currentHashSecureRandom, SecureRandom currentKeySecureRandom)
    {
        try {
            return HashFunctions.encryptAES256(encrypt, generateNewHash(byteSizeHash, currentHashSecureRandom), getPasswordIterations(currentPasswdIterations), generateNewKey(byteSizeKey,currentKeySecureRandom));
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static String decrypt(String decrypt, int byteSizeHash, int byteSizeKey, int currentPasswdIterations, SecureRandom currentHashSecureRandom, SecureRandom currentKeySecureRandom)
    {
        try {
            return HashFunctions.decryptAES256(decrypt, generateNewHash(byteSizeHash, currentHashSecureRandom), getPasswordIterations(currentPasswdIterations), generateNewKey(byteSizeKey,currentKeySecureRandom));
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static int getByteSizeHash()
    {
        return getByteSizeHash(byteSizeHash);
    }
    
    public static int getByteSizeKey()
    {
       return getByteSizeKey(byteSizeKey);
    }
    
    public static int getPasswordIterations()
    {
        return getPasswordIterations(currentPasswdIterations);
    }
    
    public static int getByteSizeHash(int byteSizeHash)
    {
        byteSizeHash++;
        if(byteSizeHash<500||byteSizeHash>=1000)
        {
            byteSizeHash=500;
        }
        return byteSizeHash;
    }
    
    public static int getByteSizeKey(int byteSizeKey)
    {
        byteSizeKey++;
        if(byteSizeKey<1000||byteSizeKey>=1500)
        {
            byteSizeKey=1000;
        }
        return byteSizeKey;
    }
    
    public static int getPasswordIterations(int currentPasswdIterations)
    {
        currentPasswdIterations++;
        if(currentPasswdIterations<65535||currentPasswdIterations>=100000)
        {
            currentPasswdIterations=65535;
        }
        return currentPasswdIterations;
    }
}
