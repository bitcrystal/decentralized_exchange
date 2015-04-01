/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange.security;

/**
 *
 * @author ABC
 */
import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class HashFunctions {

    public static String encryptRSA(String encrypt, String publicKey) {
        try {
            return encryptRSA(encrypt, stringToPubKey(publicKey));
        } catch (Exception ex) {
            System.out.println(ex);
            return "";
        }
    }

    public static String decryptRSA(String decrypt, String privateKey) {
        try {
            return decryptRSA(decrypt, stringToPrivKey(privateKey));
        } catch (Exception ex) {
            System.out.println(ex);
            return "";
        }
    }
    
    public static String encryptAES256(String plainText, String password, String salt) {
        try {
            return encryptAES256(plainText, password, 65535, salt);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String decryptAES256(String plainText, String password, String salt) {
        try {
            return decryptAES256(plainText, password, 65535, salt);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String encryptAES256(String plainText, String password, int pswdIterations, String salt) throws Exception {

        //get salt
        byte[] saltBytes = salt.getBytes("UTF-8");

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                pswdIterations,
                128);


        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        //encrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        String string = Base64.encodeBase64String(encryptedTextBytes) + ";;;" + Base64.encodeBase64String(ivBytes);
        return Base64.encodeBase64String(string.getBytes("UTF-8"));
    }

    @SuppressWarnings("static-access")
    public static String decryptAES256(String encryptedText, String password, int pswdIterations, String salt) throws Exception {
        encryptedText = new String(Base64.decodeBase64(encryptedText), "UTF-8");

        byte[] saltBytes = salt.getBytes("UTF-8");
        if (!encryptedText.contains(";;;")) {
            return "";
        }
        String[] split = encryptedText.split(Pattern.quote(";;;"));
        encryptedText = split[0];
        byte[] ivBytes = Base64.decodeBase64(split[1]);
        byte[] encryptedTextBytes = Base64.decodeBase64(encryptedText);

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                pswdIterations,
                128);

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // cipher.init(Cipher.ENCRYPT_MODE, secret);
        //AlgorithmParameters params = cipher.getParameters();
        //byte ivBytes[] = params.getParameterSpec(IvParameterSpec.class).getIV();
        // Decrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

        byte[] decryptedTextBytes = null;
        try {
            decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return new String(decryptedTextBytes, "UTF-8");
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
            return hash("SHA-256", base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public static String sha512(String base) {
        try {
            return hash("SHA-512", base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public static String md5(String base) {
        try {
            return hash("MD5", base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public static String sha1(String base) {
        try {
            return hash("SHA-1", base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public static String sha384(String base) {
        try {
            return hash("SHA-384", base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public static String md2(String base) {
        try {
            return hash("MD2", base);
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public static String encryptRSA(String encrypt, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.ENCRYPT_MODE, publicKey);
        String encodeBase64 = Base64.encodeBase64String(rsa.doFinal(encrypt.getBytes("UTF-8")));
        return encodeBase64;
    }

    public static String decryptRSA(String decrypt, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.decodeBase64(decrypt)), "UTF-8");
    }

    public static String hash(String hashName, String base) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance(hashName);
        byte[] hash = digest.digest(base.getBytes("UTF-8"));
        BigInteger number = new BigInteger(1, hash);
        return number.toString(16);
    }

    public static String pubKeyToString(PublicKey pubKey) {
        byte[] array = pubKey.getEncoded();
        String string = Base64.encodeBase64String(array);
        return string;
    }

    public static PublicKey stringToPubKey(String key) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] decodeBuffer = Base64.decodeBase64(key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(decodeBuffer);
        KeyFactory keyFact = KeyFactory.getInstance("RSA");
        PublicKey generatePublic = keyFact.generatePublic(x509KeySpec);
        return generatePublic;
    }

    public static String privKeyToString(PrivateKey pubKey) {
        byte[] array = pubKey.getEncoded();
        String string = Base64.encodeBase64String(array);
        return string;
    }

    public static PrivateKey stringToPrivKey(String key) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] decodeBuffer = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec x509KeySpec = new PKCS8EncodedKeySpec(decodeBuffer);
        KeyFactory keyFact = KeyFactory.getInstance("RSA");
        PrivateKey generatePrivate = keyFact.generatePrivate(x509KeySpec);
        return generatePrivate;
    }

    public static KeyPairGenerator getDefaultKeygenGenerator() throws NoSuchAlgorithmException, NoSuchProviderException {
        return getKeygenGenerator(1024);
    }

    public static KeyPairGenerator getKeygenGenerator(int size) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (size <= 0) {
            size = 1024;
        }
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(size);
        return keyGen;
    }

    public static KeyPair getKeyPair(KeyPairGenerator generator) {
        return generator.genKeyPair();
    }

    public static PublicKey getPublicKey(KeyPair keyPair) {
        return keyPair.getPublic();
    }

    public static PrivateKey getPrivateKey(KeyPair keyPair) {
        return keyPair.getPrivate();
    }

    public static Map<String, Object> getKeys(KeyPairGenerator generator) {
        KeyPair keyPair = generator.genKeyPair();
        PublicKey publicKey = getPublicKey(keyPair);
        PrivateKey privateKey = getPrivateKey(keyPair);
        Map<String, Object> map = new ConcurrentHashMap<String, Object>();
        map.put("privateKey", privateKey);
        map.put("publicKey", publicKey);
        return map;
    }

    public static String getHashStringSHA256(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        String sha256 = "";
        while (true) {
            sha256 = HashFunctions.sha256(string);
            if (sha256.startsWith("000000")) {
                break;
            }
        }
        return string;
    }

    public static String getHashStringSHA256_X2(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        while (true) {
            string = HashFunctions.sha256(HashFunctions.sha256(string));
            if (string.startsWith("000000")) {
                break;
            }
        }
        return string;
    }

    public static String getHashStringSHA256_SHA512(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        while (true) {
            string = HashFunctions.sha256(HashFunctions.sha512(string));
            if (string.startsWith("000000")) {
                break;
            }
        }
        return string;
    }
}
