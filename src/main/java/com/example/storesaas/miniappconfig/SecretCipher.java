package com.example.storesaas.miniappconfig;

import com.example.storesaas.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecretCipher {
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private final SecretKeySpec key;
    private final SecureRandom secureRandom = new SecureRandom();

    public SecretCipher(@Value("${miniapp.secret-key}") String encodedKey) {
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(encodedKey);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("MINIAPP_SECRET_KEY必须是Base64编码", ex);
        }
        if (decoded.length != 32) {
            throw new IllegalStateException("MINIAPP_SECRET_KEY解码后必须为32字节");
        }
        this.key = new SecretKeySpec(decoded, "AES");
    }

    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_BYTES];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(ciphertext, 0, payload, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("小程序密钥加密失败", ex);
        }
    }

    public String decrypt(String payload) {
        try {
            byte[] decoded = Base64.getDecoder().decode(payload);
            if (decoded.length <= IV_BYTES) throw new GeneralSecurityException("密文长度不合法");
            byte[] iv = java.util.Arrays.copyOfRange(decoded, 0, IV_BYTES);
            byte[] ciphertext = java.util.Arrays.copyOfRange(decoded, IV_BYTES, decoded.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new BusinessException("小程序密钥无法解密，请重新配置");
        }
    }
}
