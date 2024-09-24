package com.all.homedesire.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String SECRET_KEY = "================AHD===============";  // Ensure it's long enough
    private static final String SALT = "AHD";

    // Method to encrypt and URL encode the string
    public String encrypt(String strToEncrypt) {
        try {
            byte[] saltBytes = SALT.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = generateSecretKey(SECRET_KEY, saltBytes);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  // Ensure consistent padding
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Encrypt and then encode using Base64
            String encryptedString = Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
            
            // Encode the Base64 string for URL-safe transmission
            return URLEncoder.encode(encryptedString, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e.toString());
        }
    }

    // Method to decode the URL-encoded string and then decrypt it
    public String decrypt(String strToDecrypt) {
        try {
            // First, decode the URL-encoded Base64 string
            String decodedUrlString = URLDecoder.decode(strToDecrypt, "UTF-8");

            byte[] saltBytes = SALT.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = generateSecretKey(SECRET_KEY, saltBytes);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  // Ensure consistent padding
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Decode the Base64 string and then decrypt
            return new String(cipher.doFinal(Base64.getDecoder().decode(decodedUrlString)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: " + e.toString());
        }
    }

    // Method to generate a SecretKeySpec using the secret and salt
    private SecretKeySpec generateSecretKey(String secret, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }
}
