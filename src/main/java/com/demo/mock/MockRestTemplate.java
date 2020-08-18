package com.demo.mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class MockRestTemplate extends RestTemplate {
    @Override
    public void put(String url, @Nullable Object request, Object... uriVariables) throws RestClientException {
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(
            String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return (ResponseEntity<T>) new ResponseEntity<>("someHash", HttpStatus.OK);
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return (ResponseEntity<T>) new ResponseEntity<>("someSalt", HttpStatus.OK);
    }
}
