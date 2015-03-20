/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange.security;

/**
 *
 * @author ABC
 */
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class HashFunctions {

    public static String encryptAES256(String plainText, String password, int pswdIterations, String salt) throws Exception {

        //get salt
        byte[] saltBytes = salt.getBytes("UTF-8");

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                pswdIterations,
                256);

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        //encrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return new Base64().encodeAsString(encryptedTextBytes);
    }

    @SuppressWarnings("static-access")
    public static String decryptAES256(String encryptedText, String password, int pswdIterations, String salt) throws Exception {

        byte[] saltBytes = salt.getBytes("UTF-8");
        byte[] encryptedTextBytes = new Base64().decodeBase64(encryptedText);

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                pswdIterations,
                256);

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte ivBytes[] = params.getParameterSpec(IvParameterSpec.class).getIV();
        // Decrypt the message
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

        byte[] decryptedTextBytes = null;
        try {
            decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return new String(decryptedTextBytes);
    }

    public static String generateSalt(int byteSize, long seed) {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(seed);
        if (byteSize < 1) {
            return "";
        }
        byte bytes[] = new byte[byteSize];
        secureRandom.nextBytes(bytes);
        String s = new String(bytes);
        return s;
    }

    public static String generateSalt(int byteSize, SecureRandom secureRandom) {
        if (!(secureRandom instanceof SecureRandom)) {
            return "";
        }
        if (byteSize < 1) {
            return "";
        }
        byte bytes[] = new byte[byteSize];
        secureRandom.nextBytes(bytes);
        String s = new String(bytes);
        return s;
    }

    public static String sha256(String base) {
        try {
            return hash("SHA-256",base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
    
    public static String sha512(String base) {
        try {
            return hash("SHA-512",base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
    
    public static String md5(String base) {
        try {
            return hash("MD5",base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
    
    public static String sha1(String base)
    {
         try {
            return hash("SHA-1",base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
    
    public static String sha384(String base)
    {
         try {
            return hash("SHA-384",base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
    
    public static String md2(String base)
    {
         try {
            return hash("MD2",base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
    
    public static String hash(String hashName, String base) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            MessageDigest digest = MessageDigest.getInstance(hashName);
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            BigInteger number = new BigInteger(1,hash);
            return number.toString(16);
    }
}
