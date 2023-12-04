package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MyController {
    
    @Value("${transit.path}")
    private String transitPath;

    public String encryptValue(String value, String keyName) {
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Plaintext plaintext = Plaintext.of(value);
        String cipherText = vaultOps.opsForTransit(transitPath).encrypt(keyName, plaintext).getCiphertext();
        return cipherText;
    }

    public String decryptValue(String value, String keyName) {
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Ciphertext ciphertext = Ciphertext.of(value);
        String plaintext = vaultOps.opsForTransit(transitPath).decrypt(keyName, ciphertext).asString();
        return plaintext;
    }

    @Value("${username}")
    String username;

    @Value("${password}")
    String password;

    @GetMapping("/kv/secret")
    public Map<String, String> getSecret() {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        return map;
    }

    @PostMapping("/transit/encrypt")
    public Map<String, String> handleEncrypt(@RequestBody String requestBody) {
        Map<String, String> map = new HashMap<>();
        String cipherText = encryptValue(requestBody, "test");
        map.put("encrypted_value:", cipherText);
        return map;
    }

    @PostMapping("/transit/decrypt")
    public Map<String, String> handleDecrypt(@RequestBody String requestBody) {
        Map<String, String> map = new HashMap<>();
        String cipherText = decryptValue(requestBody, "test");
        map.put("decrypted_value:", cipherText);
        return map;
    }
}