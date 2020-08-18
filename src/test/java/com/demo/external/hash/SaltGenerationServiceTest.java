package com.demo.external.hash;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(SaltGenerationService.class)
public class SaltGenerationServiceTest {
    @Autowired
    private SaltGenerationService target;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void generateRandomSalt() {
        server.expect(requestTo("http://localhost:8110/salt"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("SomeSalt", MediaType.TEXT_PLAIN));

        assertThat(target.generateRandomSalt()).isEqualTo("SomeSalt");
    }
}
