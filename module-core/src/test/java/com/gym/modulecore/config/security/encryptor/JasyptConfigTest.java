package com.gym.modulecore.config.security.encryptor;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JasyptConfigTest {
    private final static String KEY = "communitySystem";

    @Test
    void jasypt_암호화() {
        String plainText = "nas_ntels_suhyang_ezwel.community-2023.secret_key";

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setPassword(KEY);

        String encryptedText = jasypt.encrypt(plainText);
        String decryptedText = jasypt.decrypt(encryptedText);

        System.out.println(encryptedText);

        assertThat(plainText).isEqualTo(decryptedText);
    }

    @Test
    void jasypt_복호화() {
        String encryptedText = "DXUafY6Wwez8jqvGrq6/9LqTw0gjtDjC";

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setPassword(KEY);

        String decryptedText = jasypt.decrypt(encryptedText);

        System.out.println(decryptedText);
    }
}