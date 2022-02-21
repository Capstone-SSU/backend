package com.example.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
public class AES256 {

    @Value("${auth.aesSecret}")
    private void setKey(String temp){
        this.getkey=temp;
    }

    private String getkey; // encryption, decryption key


    public static  String algorithm="AES";



    public String encryption(String plain) throws Exception {
        Cipher cipher=Cipher.getInstance(algorithm);
        String key=getkey.substring(0,32); // aes 256 key 32byte
        String encodedBase64Key=encodeKey(key);
        Key keySpec=generateKey(key);

        cipher.init(Cipher.ENCRYPT_MODE,keySpec);
        byte[] encrypt=cipher.doFinal(plain.getBytes());
        //        System.out.println("encrypted: "+encrypted);

        return Base64.getEncoder().encodeToString(encrypt);
    }

    private static Key generateKey(String secret) {
        byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
        return new SecretKeySpec(decoded, algorithm);
    }

    public static String encodeKey(String str) {
        byte[] encoded = Base64.getEncoder().encode(str.getBytes());
        return new String(encoded);
    }
}
