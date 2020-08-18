package com.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class HashServiceTest {
    @InjectMocks
    private HashService target;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {
        String salt = target.generateRandomSalt();
        String hashed1 = target.hash("someClearText", salt);
        String hashed2 = target.hash("someClearText", salt);
        String hashed3 = target.hash("someClearText", salt + "1");
        String hashed4 = target.hash("someClearText" + "1", salt);

        assertThat(hashed1).isEqualTo(hashed2);
        assertThat(hashed1).isNotEqualTo(hashed3);
        assertThat(hashed1).isNotEqualTo(hashed4);
    }
}
