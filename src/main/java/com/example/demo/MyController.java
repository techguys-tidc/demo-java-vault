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

    public String encryptValue(String value, String keyName) {
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Plaintext plaintext = Plaintext.of(value);

        String cipherText = vaultOps.opsForTransit("transit/").encrypt(keyName, plaintext).getCiphertext();
        return cipherText;
    }

    public String decryptValue(String value, String keyName) {
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Ciphertext ciphertext = Ciphertext.of(value);
        String plaintext = vaultOps.opsForTransit().decrypt(keyName, ciphertext).asString();
        return plaintext;
    }

    @Value("${username}")
    String username;

    @Value("${password}")
    String password;

    @Value("${username.encrypt}")
    String usernameEncrypt;

    @Value("${password.encrypt}")
    String passwordEncrypt;

    @GetMapping("/secret")
    public Map<String, String> getSecret() {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);

        String usernameCipherText = decryptValue(usernameEncrypt, "test");
        String passwordCipherText = decryptValue(passwordEncrypt, "test");

        map.put(usernameEncrypt, usernameCipherText);
        map.put(passwordEncrypt, passwordCipherText);

        return map;
    }

    @PostMapping("/encrypt")
    public Map<String, String> handleEncrypt(@RequestBody String requestBody) {
        Map<String, String> map = new HashMap<>();

        String cipherText = encryptValue(requestBody, "test");

        System.out.println("Received POST request with body: " + requestBody);

        System.out.println("Encrypted value");
        System.out.println("-------------------------------");
        System.out.println(cipherText);
        System.out.println("-------------------------------");
        System.out.println();

        map.put("encrypted_value:", cipherText);

        return map;
    }

    @PostMapping("/decrypt")
    public Map<String, String> handleDecrypt(@RequestBody String requestBody) {
        Map<String, String> map = new HashMap<>();

        String cipherText = decryptValue(requestBody, "test");
        System.out.println("Decrypted value");
        System.out.println("-------------------------------");
        System.out.println(cipherText);
        System.out.println("-------------------------------");
        System.out.println();

        map.put("decrypted_value:", cipherText);

        return map;
    }
}