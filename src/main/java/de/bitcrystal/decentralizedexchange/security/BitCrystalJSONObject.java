/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange.security;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ConcurrentConfig;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.crypto.KeyGenerator;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ABC
 */
public class BitCrystalJSONObject {

    private String firstPrivKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKnE/oP7CSNjAstnS7Y9L84nDilggZFkyeWWi0PoldajiL9TPDDsOiwNemndj3Xy3hxUfCgk8x0abbpn03ZOcuJtpnC0FY+U/5K2y8NOAW0vwNvZ6Xqkhef89Jn3ru1E5l3qK2PaN3fLB19gD9wzOkajl1czAAEA+HUVnUvvkjcRAgMBAAECgYAigyymkgMSkY9A4HfwzVGJnEDxwm+0qgOtS8R2ArNrVgV3t6kV3QAacob/PI5skPn8UmSHGLPfp77E1VSUyW/rKMu4mjhUIZLZb/ibTSB0gFvC1yVFFJ/TEfFaLB6GjyMtD/ZnL3xMYRwvKwhE1Fc8eM/4uXXu8TjZF66pRN05UQJBAOkEBsdTQ6fHPhExQZEACQsBv94tcuiqCGdpBKADk9Hfdu2GB5h/iTCRljhN+sI4/cAHP/dsM4Pa1uStvIGV4K0CQQC6g+l+XukRYgHuI4+2cEmXlsEUWL4VzjyWtZDRYdvPQ3a7GFH6mEmnpl/4BOei2OEgqF45M4dBXIV9ZYV8Rah1AkBtqzx5geHczHSU6ObgFPEsogUoz6E+ihBsg0vu9+ARriguQJ/AkX4DQvI9y83SrboYMBFWJTHVxCDR/kczglP9AkA3iHkoCChK5ax/dBUhE9PFj7xTS4zlPy+pC8xfm0xwLL0YPciC3X7orM/cL9GsGfkF2TucumvmGR8h0+1nr/HVAkBtvRrDldVXrLOEBPysp1+71Ari7D/DcUkNaUQs0vxZrCZT3OgKV6S4caFgPtphLtoFADflBbJulrHj7d7/VliC";
    private String firstPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpxP6D+wkjYwLLZ0u2PS/OJw4pYIGRZMnllotD6JXWo4i/Uzww7DosDXpp3Y918t4cVHwoJPMdGm26Z9N2TnLibaZwtBWPlP+StsvDTgFtL8Db2el6pIXn/PSZ967tROZd6itj2jd3ywdfYA/cMzpGo5dXMwABAPh1FZ1L75I3EQIDAQAB";
    private String secondPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJgKZl6USu5yFTlLfHynJJdN4uXHYBYOIPDnQ/9miBpwurNa00Vb08DDDigATH+/EDk8yPyYKoqSpYqLHgS3UwrZfF+RCdjiPvv9bK+PeCPNlnjhoe+Savtps6VlsAyx7AETBVxtSME5C7vX8sA9zbjVNHDo4T5TbXry13CIcZbwIDAQAB";
    private String secondPrivKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAImApmXpRK7nIVOUt8fKckl03i5cdgFg4g8OdD/2aIGnC6s1rTRVvTwMMOKABMf78QOTzI/JgqipKlioseBLdTCtl8X5EJ2OI++/1sr494I82WeOGh75Jq+2mzpWWwDLHsARMFXG1IwTkLu9fywD3NuNU0cOjhPlNtevLXcIhxlvAgMBAAECgYALuvz3HsJsRW2TatGO6td7Ksw1mk+66GAAttP2DPh3bx/QuGCRYWsmb0fQWumNghHao0ZFUgSijOP+8k1SfD2ADBi62nt3xO+4Ux9/iGyd5bjg2sjD5dfHz+zsKFB15VZKJkxeAX/z3DayLSsWiyxXTDVLRYyNPfYYFQuEpryfGQJBAOBaAJftFYN2i5fgOUhcFdFUsXoIWUbZr+QChXjzmOZCLhurTTug4m1gfomlzgT/moO7eT6NHKnXdLIAxbYVCxMCQQCc5kXOAfUXBIN7cUvd7iXA+AZEtqs/wRZdHfIG0EvV1a4ABvcmhCQrf0v/jBcmVrUtGOdejKyS/sWzhSsynEe1AkB6pjl01RPRztj5qocmW9FBoz9zK2VDKnbR+97kBKguGTgDwF50nruqd/o1XgOHEAV0xuS9oX9uCm+tcGal6FMpAkBI+I/ej+p9GgxMjRTL0sacSL4hlxNuQ08zX4oAwT+D5C1YteScdTidQkcGQxFBM3Jv1vVEdAiPFQ69v2Gn2CdBAkEAxpIrmiKLats++hZb93Fk6r5oR14IyNzIWihq/n9U8uHocz1ZnR5QRXjxElxXk8yQAY4Qn3Kfx36rfKQ2YtPiLQ==";
    private String thirdPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3J5g6bA0URtpTtobzgX5CMfDudU41AMNPaY5Ya2+kmbLzlYNtmNE33yn7IB0xearKF8b86mal4J0A6Q8LGY64939B3ZgCO3cs7QCIs+CyYAvy9eLs+QXdoJiqUKbL/JqMYKdc/umaZdL6uwnmO5HaF8vRkwGp6K6hDAJSg5PZVwIDAQAB";
    private String thirdPrivKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALcnmDpsDRRG2lO2hvOBfkIx8O51TjUAw09pjlhrb6SZsvOVg22Y0TffKfsgHTF5qsoXxvzqZqXgnQDpDwsZjrj3f0HdmAI7dyztAIiz4LJgC/L14uz5Bd2gmKpQpsv8moxgp1z+6Zpl0vq7CeY7kdoXy9GTAanorqEMAlKDk9lXAgMBAAECgYB6flGyaQeKUyS7riyteSKvNOV1JiFLE5qMZwVbn2cIh/8GKC3BQ95RZsCWAtFjgIK4LJTaHA7Qy9tVe4mjG9G2Qfq0aWfpeGOMOcJk3rUdWHMzGKhRilImIsNzq0xHnZsQjct8RTmnhhCcrZ6W3xEwLoOqn90uj6Qk2pP6abI48QJBAPGBkerxEvs0mzLFpsYO/yqkQ6a+2kC8BSsO+pi8MBeIHhUfTsRtpLdkWyvYfHp8snm4hWQUP3XdwyQNFIhfd08CQQDCJYeZqZ86HBlwLde1EmJw+GOBL8zH1oaY+ylDWYjQbuGJe45ohTfx3fhjThluJUMAAYAb71K+Eu1YLjelZvt5AkEA2f7gTReAz7pDoUfia4Nb9wzBeEFsVuShMaXatIWiT4txxBU3j319IoF2/pPygMoQfGkAFz9jrSwZGnCoKRUrZwJAMnGV3fMYj/ylD2SZ204/jaK8F+DXkETJDA6WFg25Yo6sww/7XGTguFM4KnUsXlYIJoQ9SQTRttksGfFIPA2Z4QJADnMsTVk1aexsunW+QEN7kEGFEtOklCYcwD10TD0KmfRfLfzJPA/umb7MITrrSKB+VZe4iUhPzh0HNI+5rTLgSw==";
    private String firstPassword = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJgKZl6USu5yFTlLfHynJJdN4uXHYBYOIPDnQ/9miBpwurNa00Vb08DDDigATH+/EDk8yPyYKoqSpYqLHgS3UwrZfF+RCdjiPvv9bK+PeCPNlnjhoe+Savtps6VlsAyx7AETBVxtSME5C7vX8sA9zbjVNHDo4T5TbXry13CIcZbwIDAQAB";
    private String firstSalt = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAImApmXpRK7nIVOUt8fKckl03i5cdgFg4g8OdD/2aIGnC6s1rTRVvTwMMOKABMf78QOTzI/JgqipKlioseBLdTCtl8X5EJ2OI++/1sr494I82WeOGh75Jq+2mzpWWwDLHsARMFXG1IwTkLu9fywD3NuNU0cOjhPlNtevLXcIhxlvAgMBAAECgYALuvz3HsJsRW2TatGO6td7Ksw1mk+66GAAttP2DPh3bx/QuGCRYWsmb0fQWumNghHao0ZFUgSijOP+8k1SfD2ADBi62nt3xO+4Ux9/iGyd5bjg2sjD5dfHz+zsKFB15VZKJkxeAX/z3DayLSsWiyxXTDVLRYyNPfYYFQuEpryfGQJBAOBaAJftFYN2i5fgOUhcFdFUsXoIWUbZr+QChXjzmOZCLhurTTug4m1gfomlzgT/moO7eT6NHKnXdLIAxbYVCxMCQQCc5kXOAfUXBIN7cUvd7iXA+AZEtqs/wRZdHfIG0EvV1a4ABvcmhCQrf0v/jBcmVrUtGOdejKyS/sWzhSsynEe1AkB6pjl01RPRztj5qocmW9FBoz9zK2VDKnbR+97kBKguGTgDwF50nruqd/o1XgOHEAV0xuS9oX9uCm+tcGal6FMpAkBI+I/ej+p9GgxMjRTL0sacSL4hlxNuQ08zX4oAwT+D5C1YteScdTidQkcGQxFBM3Jv1vVEdAiPFQ69v2Gn2CdBAkEAxpIrmiKLats++hZb93Fk6r5oR14IyNzIWihq/n9U8uHocz1ZnR5QRXjxElxXk8yQAY4Qn3Kfx36rfKQ2YtPiLQ==";
    private String secondPassword = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3J5g6bA0URtpTtobzgX5CMfDudU41AMNPaY5Ya2+kmbLzlYNtmNE33yn7IB0xearKF8b86mal4J0A6Q8LGY64939B3ZgCO3cs7QCIs+CyYAvy9eLs+QXdoJiqUKbL/JqMYKdc/umaZdL6uwnmO5HaF8vRkwGp6K6hDAJSg5PZVwIDAQAB";
    private String secondSalt = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALcnmDpsDRRG2lO2hvOBfkIx8O51TjUAw09pjlhrb6SZsvOVg22Y0TffKfsgHTF5qsoXxvzqZqXgnQDpDwsZjrj3f0HdmAI7dyztAIiz4LJgC/L14uz5Bd2gmKpQpsv8moxgp1z+6Zpl0vq7CeY7kdoXy9GTAanorqEMAlKDk9lXAgMBAAECgYB6flGyaQeKUyS7riyteSKvNOV1JiFLE5qMZwVbn2cIh/8GKC3BQ95RZsCWAtFjgIK4LJTaHA7Qy9tVe4mjG9G2Qfq0aWfpeGOMOcJk3rUdWHMzGKhRilImIsNzq0xHnZsQjct8RTmnhhCcrZ6W3xEwLoOqn90uj6Qk2pP6abI48QJBAPGBkerxEvs0mzLFpsYO/yqkQ6a+2kC8BSsO+pi8MBeIHhUfTsRtpLdkWyvYfHp8snm4hWQUP3XdwyQNFIhfd08CQQDCJYeZqZ86HBlwLde1EmJw+GOBL8zH1oaY+ylDWYjQbuGJe45ohTfx3fhjThluJUMAAYAb71K+Eu1YLjelZvt5AkEA2f7gTReAz7pDoUfia4Nb9wzBeEFsVuShMaXatIWiT4txxBU3j319IoF2/pPygMoQfGkAFz9jrSwZGnCoKRUrZwJAMnGV3fMYj/ylD2SZ204/jaK8F+DXkETJDA6WFg25Yo6sww/7XGTguFM4KnUsXlYIJoQ9SQTRttksGfFIPA2Z4QJADnMsTVk1aexsunW+QEN7kEGFEtOklCYcwD10TD0KmfRfLfzJPA/umb7MITrrSKB+VZe4iUhPzh0HNI+5rTLgSw==";
    private String thirdPassword = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCk0R6LaYtSeX1/VfTaalssx8A/CTrg3sGUP4VvI5gpay+/FtF2IMyIv8DMhsqh/IYdeeHlNuHmfuBLcx7lBSRrggOYIynzegTif4xbEiU1qAVCWTbTRhjvK0wAXX645PNO/jVRFheb3lopfQ2EId70qAgVoa5hKmggoQYwYfgv7QIDAQAB";
    private String thirdSalt = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKTRHotpi1J5fX9V9NpqWyzHwD8JOuDewZQ/hW8jmClrL78W0XYgzIi/wMyGyqH8hh154eU24eZ+4EtzHuUFJGuCA5gjKfN6BOJ/jFsSJTWoBUJZNtNGGO8rTABdfrjk807+NVEWF5veWil9DYQh3vSoCBWhrmEqaCChBjBh+C/tAgMBAAECgYBwVYe4BYvL/romm/NzGqJh9xN041maArl1zYWopl4DfObiGy9QkDJX0joaLJTXq0HNvVDV5X46l6dQRXp9JfAXq67XhfCW6D47OsiIBXvNVoK9y2INfhA8ABnoBxGJrgsd/iX5XwqJRNPW1K7G4Nodu9mVSNvFLJpgKt3lLXtvAQJBAOvATFpk7jOosXUmzFxE3VrRiy9DjQgeyf0PAi9+IHtQegN7hC5RljN3mrSyE8sHL1BcEGj3zqkxOMcUSXU6lfECQQCy+R3FGdwqheUEOzjwCYwe/EJIGN2otF+i+5aIFl6ZVng4Bs6kToOS1KT4g0LzUjJiSj12+HgX5qozIhEKfk29AkBfEix4Y61/3a66vMu7BfZyUoCbrdUpCimQyMWr22RD9n9N+jii6Tn2RWx5JDtBLAOTl5zyh8SqjHQGLJM3GEoxAkEAngQzE54kdGrpbBX5zH0xeP4Sa2OA+X3f3p+xJZBSj6H33Met1QX5Rz92NHB4QSr2jy+rGwp/xeKDrUfLse8YJQJAUy9apsO6rZ0DGqouAdJeqsrwRNRktFWshdO7IxCvVSB8lXeEz4t0sRau2Y3Y+hCfzXzG8SDdtL4JAiJMs1K5Mg==";
    private String fourthPassword = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSEPgvqP7LPWQBUQ0Ba2QAqM+YZccoXvJv7Ktu/C0vTRGi2V27wfy1xeHD6y3vmoYMZUu30aoGgjBFlMin6ErP8X2DUxmw1K3J9CzNHhV1KusoLfvU0GSe23hwt7rvXbkobrKVuUiTJNwbg5tmXZDg5nzuQgLIEE+Tzgb96VC7IwIDAQAB";
    private String fourthSalt = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJIQ+C+o/ss9ZAFRDQFrZACoz5hlxyhe8m/sq278LS9NEaLZXbvB/LXF4cPrLe+ahgxlS7fRqgaCMEWUyKfoSs/xfYNTGbDUrcn0LM0eFXUq6ygt+9TQZJ7beHC3uu9duShuspW5SJMk3BuDm2ZdkODmfO5CAsgQT5POBv3pULsjAgMBAAECgYAaDwT4G8I5KB6rTXZwhmQ06ej/W0kInsfJyANdrYeyJKG81+KtGJKpK0B334E2ee7oTzoO9UzMFHXgNAcITZzq7vIcZzXkv/UXeLOsL8BqHRaAnfomD5T3AEIwFQvnTh9v80Z2peFDb2ogmeh9m+uFQyvv9Vj2do4XhW9cDN6tgQJBANRG5VmFYAoUAD0wYmTvfWh8wmZMUQMzdPAAOhEv5jyiHaeacJgyWAIRRrUBh0Q1b+5U5b5BN6v2o+EfONbprZECQQCwJt3hW00SjV0pj58cA67Fe6wvUj1vlsn91er/Q2LxeSzgAIsnZQZ8F0oT2prG1LcmjFU1LPFa4GFEEr1cFhNzAkEAwks1xy91zSOraFYt6TbvMUDn6hw2Flj10mpSu8J82MiFng+UuSlzNkk7z5bbiUnj7mAUfIRTAXnZW8dpDgSKkQJBAIs5/Q88kZtDPko2KSUESCPwjvRMBSDoxrgpVvbw9Qg0pWJbK6E0eT6lTbiJhOH4iNYf/szru8v/IwGh6icZj78CQQCHfqmuUB8vmwWdPRML2CmtKJo/uRmfZLqrFBtFsQM9IsSRSO4srk3NQ4wRE8oJGKXeVdiHugEnYgWcRJr2ADSa";
    private String fivePassword = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCG7UvfdG0EkOorlIfqVwx07ltsdqv8+ZcqG3/locOHvpB0QaelMfJP1whHSuQ9dIvHPNHeBukFKgiSQ8Oew+sVemDQXIU3wLzxdf8DDCzjf2WIm5WLpUEzg7kKmoU+C/8WKTC+Tr7QPEvGswVd7HnSP7Av7lLhOMvrAzBrTqz/6QIDAQAB";
    private String fiveSalt = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIbtS990bQSQ6iuUh+pXDHTuW2x2q/z5lyobf+Whw4e+kHRBp6Ux8k/XCEdK5D10i8c80d4G6QUqCJJDw57D6xV6YNBchTfAvPF1/wMMLON/ZYiblYulQTODuQqahT4L/xYpML5OvtA8S8azBV3sedI/sC/uUuE4y+sDMGtOrP/pAgMBAAECgYAwBOvnzuutoFV2xRnKEMjiJKJs658yHTHrTnYqJ3QLL4sBlQwxAqGWQJU1qjWomX3VnpOiTRtJNzhttag9LMTRDnyNartnyCjccwOmbeEcvJrKL/JYoCTXR8YsbfAkpMrG+d2Jx4UT13N42eBPTsOP7t+rNkSgNEmmeluhs9GXMQJBANVS6yAagmNlYSZUtjTo+x5TwfYSrwetQnUJqVQy/EsLG0ccXTxb3Ns70mUzdvNSnMbwi9kgOH3SPaw5cO4FGhcCQQCh62N3PPpA7fg7SwOjKDhO5g/CZMghGJRqKd4tpDIoZUDFJEasoZuRGZwWZriqW/N1VBlTwwejpG406qP7YPX/AkBbuEUkDoHVXreAlZep9CpUhcq1lJ7w/AvA6qCFdU6IrYPS9V0ZIJ47HON/Y7tXL0P9PVvDxVjEsGqX7DKkBEmNAkBKVQtWg/HGuPhKEAfdcOtYnRkC/s05FFWd3xaWEVjNXp47YonnWlFWbVFQn1uLKac8Z50w7MmnACdvt4AMONj1AkEA1EQ3lK7W9v6thMH9e4iKMxUVKL3o23CQC7Cai/ECvKd8jOz22umZ7so/0S4JiWhu7Z29NdX+N0TaEqaCGsXxDA==";
    private KeyPairGenerator keyPairGenerator = null;
    
    public void setPasswordSaltPair(String password, String salt, int count) {
        switch (count) {
            case 0: {
                firstPassword = password;
                firstSalt = salt;
            }
            break;

            case 1: {
                secondPassword = password;
                secondSalt = salt;
            }
            break;

            case 2: {
                thirdPassword = password;
                thirdSalt = salt;
            }
            break;

            case 3: {
                fourthPassword = password;
                fourthSalt = salt;
            }
            break;

            case 4: {
                fivePassword = password;
                fiveSalt = salt;
            }
        }
    }

    public void setPubPrivKeyPair(String pubkey, String privkey, int count) {
        switch (count) {
            case 0: {
                firstPubKey = pubkey;
                firstPrivKey = privkey;
            }
            break;

            case 1: {
                secondPubKey = pubkey;
                secondPrivKey = privkey;
            }
            break;

            case 2: {
                thirdPubKey = pubkey;
                thirdPrivKey = privkey;
            }
            break;
        }
    }

    public String[] getPasswordSaltPair(int count) {
        String[] split = new String[2];
        switch (count) {
            case 0: {
                split[0] = firstPassword;
                split[1] = firstSalt;
            }
            break;

            case 1: {
                split[0] = secondPassword;
                split[1] = secondSalt;
            }
            break;

            case 2: {
                split[0] = thirdPassword;
                split[1] = thirdSalt;
            }
            break;

            case 3: {
                split[0] = fourthPassword;
                split[1] = fourthSalt;
            }
            break;

            case 4: {
                split[0] = fivePassword;
                split[1] = fiveSalt;
            }
        }
        return split;
    }

    public String[] getPubPrivKeyPair(int count) {
        String[] split = new String[2];
        switch (count) {
            case 0: {
                split[0] = firstPubKey;
                split[1] = firstPrivKey;
            }
            break;

            case 1: {
                split[0] = secondPubKey;
                split[1] = secondPrivKey;
            }
            break;

            case 2: {
                split[0] = thirdPubKey;
                split[1] = thirdPrivKey;
            }
            break;
        }
        return split;
    }
    
    public String[] genPubPrivKeyPair()
    {
        String[] split = new String[2];
        if(keyPairGenerator==null)
        {
            try {
                keyPairGenerator = HashFunctions.getDefaultKeygenGenerator();
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(BitCrystalJSONObject.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchProviderException ex) {
                Logger.getLogger(BitCrystalJSONObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        KeyPair genKeyPair = keyPairGenerator.genKeyPair();
        PublicKey aPublic = genKeyPair.getPublic();
        PrivateKey aPrivate = genKeyPair.getPrivate();
        String pubKey = HashFunctions.pubKeyToString(aPublic);
        String privKey = HashFunctions.privKeyToString(aPrivate);
        split[0]=pubKey;
        split[1]=privKey;
        return split;
    }

    public String encodeString(String obj) {
        String toJSONString = obj;
        toJSONString = HashFunctions.encryptAES256(toJSONString, firstPassword, firstSalt);
        toJSONString = HashFunctions.encryptAES256(toJSONString, secondPassword, secondSalt);
        toJSONString = HashFunctions.encryptAES256(toJSONString, thirdPassword, thirdSalt);
        toJSONString = HashFunctions.encryptAES256(toJSONString, fourthPassword, fourthSalt);
        toJSONString = HashFunctions.encryptAES256(toJSONString, fivePassword, fiveSalt);
        String substring = toJSONString.substring(0, 117);
        substring = HashFunctions.encryptRSA(substring, firstPubKey);
        String substring1 = toJSONString.substring(117, 117 * 2);
        substring1 = HashFunctions.encryptRSA(substring1, firstPubKey);
        String substring2 = toJSONString.substring(117 * 2, 117 * 3);
        substring2 = HashFunctions.encryptRSA(substring2, secondPubKey);
        String substring3 = toJSONString.substring(117 * 3, 117 * 4);
        substring3 = HashFunctions.encryptRSA(substring3, secondPubKey);
        String substring4 = toJSONString.substring(117 * 4, 117 * 5);
        substring4 = HashFunctions.encryptRSA(substring4, thirdPubKey);
        String substring5 = toJSONString.substring(117 * 5, 117 * 6);
        substring5 = HashFunctions.encryptRSA(substring5, thirdPubKey);
        String substring6 = toJSONString.substring(117 * 6, toJSONString.length());
        toJSONString = substring + "||||||||" + substring1 + "||||||||" + substring2 + "||||||||" + substring3 + "||||||||" + substring4 + "||||||||" + substring5 + "||||||||" + substring6;
        toJSONString = HashFunctions.encryptAES256(toJSONString, firstPassword, firstSalt);
        return toJSONString;
    }

    public String decodeString(String encode) {
        String toJSONString = encode;
        toJSONString = HashFunctions.decryptAES256(toJSONString, firstPassword, firstSalt);
        String[] split = toJSONString.split(Pattern.quote("||||||||"));
        split[0] = HashFunctions.decryptRSA(split[0], firstPrivKey);
        split[1] = HashFunctions.decryptRSA(split[1], firstPrivKey);
        split[2] = HashFunctions.decryptRSA(split[2], secondPrivKey);
        split[3] = HashFunctions.decryptRSA(split[3], secondPrivKey);
        split[4] = HashFunctions.decryptRSA(split[4], thirdPrivKey);
        split[5] = HashFunctions.decryptRSA(split[5], thirdPrivKey);
        toJSONString = split[0] + split[1] + split[2] + split[3] + split[4] + split[5] + split[6];
        toJSONString = HashFunctions.decryptAES256(toJSONString, fivePassword, fiveSalt);
        toJSONString = HashFunctions.decryptAES256(toJSONString, fourthPassword, fourthSalt);
        toJSONString = HashFunctions.decryptAES256(toJSONString, thirdPassword, thirdSalt);
        toJSONString = HashFunctions.decryptAES256(toJSONString, secondPassword, secondSalt);
        toJSONString = HashFunctions.decryptAES256(toJSONString, firstPassword, firstSalt);
        return toJSONString;
    }

    public String encodeWalletString(String obj) {
        try {
            String encode = encodeString(obj);
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            String encodeDataSecurityEmail = bitcrystalrpc.encodeDataSecurityEmail(encode);
            String encodeDataSecurityEmail1 = bitcoinrpc.encodeDataSecurityEmail(encodeDataSecurityEmail);
            String encodeDataSecurityEmailNeutral = bitcoinrpc.encodeDataSecurityEmailNeutral(encodeDataSecurityEmail1);
            return encodeDataSecurityEmailNeutral;
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalJSONObject.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String decodeWalletString(String decode) {
        try {
            RPCApp bitcrystalrpc = RPCApp.getAppOutRPCconf("bitcrystalrpc.conf");
            RPCApp bitcoinrpc = RPCApp.getAppOutRPCconf("bitcoinrpc.conf");
            decode = bitcoinrpc.decodeDataSecurityEmailNeutral(decode);
            decode = bitcoinrpc.decodeDataSecurityEmail(decode);
            decode = bitcrystalrpc.decodeDataSecurityEmail(decode);
            return decodeString(decode);
        } catch (Exception ex) {
            Logger.getLogger(BitCrystalJSONObject.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String encode(JSONObject obj) {
        String encode = obj.toString();
        return encodeString(encode);
    }

    public JSONObject decode(String encode) {
        try {
            String toJSONString = decodeString(encode);
            JSONObject jSONObject = new JSONObject(toJSONString);
            return jSONObject;
        } catch (JSONException ex) {
            Logger.getLogger(BitCrystalJSONObject.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String encodeWallet(JSONObject obj) {
        return encodeWalletString(obj.toString());
    }

    public JSONObject decodeWallet(String decode) {
        try {
            return new JSONObject(decodeWalletString(decode));
        } catch (JSONException ex) {
            Logger.getLogger(BitCrystalJSONObject.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean saveString(String string, String key, String path) {
        return saveString(string, key, path, "");
    }

    public boolean saveString(String string, String key, String path, String filename) {
        String fullpath = "";
        if (!path.isEmpty()) {
            if (!filename.isEmpty()) {
                fullpath = path + File.separator + filename;
            } else {
                fullpath = path;
            }
        } else {
            if (!filename.isEmpty()) {
                fullpath = filename;
            } else {
                return false;
            }
        }
        File file = new File(fullpath);
        ConcurrentConfig concurrentConfig = null;
        if (file.exists()) {
            concurrentConfig = new ConcurrentConfig(file);
            concurrentConfig.load(file, "=");
        } else {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(BitCrystalJSONObject.class.getName()).log(Level.SEVERE, null, ex);
            }
            concurrentConfig = new ConcurrentConfig(file);
        }
        Map<String, String> copyOfProperties = concurrentConfig.getCopyOfProperties();
        copyOfProperties.put(key, string);
        concurrentConfig.update(copyOfProperties);
        concurrentConfig.save("=");
        return true;
    }

    public String loadString(String key, String path) {
        return loadString(key, path, "");
    }

    public String loadString(String key, String path, String filename) {
        String fullpath = "";
        if (!path.isEmpty()) {
            if (!filename.isEmpty()) {
                fullpath = path + File.separator + filename;
            } else {
                fullpath = path;
            }
        } else {
            if (!filename.isEmpty()) {
                fullpath = filename;
            } else {
                return null;
            }
        }
        File file = new File(fullpath);
        if (!file.exists()) {
            return null;
        }
        ConcurrentConfig concurrentConfig = new ConcurrentConfig(file);
        concurrentConfig.load(file, "=");
        Map<String, String> copyOfProperties = concurrentConfig.getCopyOfProperties();
        if (!copyOfProperties.containsKey(key)) {
            return null;
        }
        return copyOfProperties.get(key);
    }

    public boolean saveJSONObject(JSONObject jsonObject, String key, String path) {
        return saveJSONObject(jsonObject, key, path, "");
    }

    public boolean saveJSONObject(JSONObject jsonObject, String key, String path, String filename) {
        String encode = encode(jsonObject);
        return saveString(encode, key, path, filename);
    }

    public JSONObject loadJSONObject(String key, String path) {
        return loadJSONObject(key, path, "");
    }

    public JSONObject loadJSONObject(String key, String path, String filename) {
        String loadString = loadString(key, path, filename);
        JSONObject decode = decode(loadString);
        return decode;
    }

    public boolean saveJSONObjectWallet(JSONObject jsonObject, String key, String path) {
        return saveJSONObjectWallet(jsonObject, key, path, "");
    }

    public boolean saveJSONObjectWallet(JSONObject jsonObject, String key, String path, String filename) {
        return saveString(encodeWallet(jsonObject), key, path, filename);
    }

    public JSONObject loadJSONObjectWallet(String key, String path) {
        return loadJSONObjectWallet(key, path, "");
    }

    public JSONObject loadJSONObjectWallet(String key, String path, String filename) {
        String loadString = loadString(key, path, filename);
        JSONObject decode = decodeWallet(loadString);
        return decode;
    }
}
