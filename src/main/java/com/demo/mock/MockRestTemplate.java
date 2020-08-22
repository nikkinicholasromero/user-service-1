package com.demo.mock;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

public class MockRestTemplate extends RestTemplate {
    @Override
    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
        return new ResponseEntity("someObject", HttpStatus.CREATED);
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(
            String url, @Nullable Object request, Class<T> responseType, Object... uriVariables) {
        return new ResponseEntity("someHash", HttpStatus.OK);
    }

    @Override
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) {
        return new ResponseEntity("someSalt", HttpStatus.OK);
    }
}
