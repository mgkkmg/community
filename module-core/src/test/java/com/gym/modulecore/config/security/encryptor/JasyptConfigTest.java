package com.gym.modulecore.config.security.encryptor;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JasyptConfigTest extends JasyptConfig {
    private final static String KEY = "";

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
        String encryptedText = "aWYPyLFWSbILA2vnBQwOcQ==";

        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
        jasypt.setPassword(KEY);

        String decryptedText = jasypt.decrypt(encryptedText);

        System.out.println(decryptedText);
    }
}