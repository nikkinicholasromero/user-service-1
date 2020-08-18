package com.demo.external.hash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(HashService.class)
public class HashServiceTest {
    @Autowired
    private HashService target;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void hash() throws JsonProcessingException {
        HashRequest request = new HashRequest("someClear", "someSalt");

        server.expect(requestTo("http://localhost:8110/hash"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(objectMapper.writeValueAsString(request)))
                .andRespond(withSuccess());

        target.hash("someClear", "someSalt");
    }
}
