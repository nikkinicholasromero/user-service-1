package com.demo.service;

import com.demo.model.Activation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ActivationGeneratorTest {
    @InjectMocks
    private ActivationGenerator target;

    @Mock
    private UuidGenerator uuidGenerator;

    private LocalDateTime localDateTime;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(target, "hoursValid", 72);

        when(uuidGenerator.generateRandomUuid()).thenReturn("someRandomUuid");

        localDateTime = LocalDateTime.now().plusHours(72);
    }

    @Test
    public void generateActivation() {
        Activation actual = target.generateActivation();
        assertThat(actual).isNotNull();
        assertThat(actual.getCode()).isEqualTo("someRandomUuid");
        assertThat(actual.getExpiration()).isNotNull();
        assertThat(actual.getExpiration().getYear()).isEqualTo(localDateTime.getYear());
        assertThat(actual.getExpiration().getMonth()).isEqualTo(localDateTime.getMonth());
        assertThat(actual.getExpiration().getDayOfYear()).isEqualTo(localDateTime.getDayOfYear());
        assertThat(actual.getExpiration().getHour()).isEqualTo(localDateTime.getHour());
    }
}
