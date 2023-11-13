package com.inev1te.study.helloworlds.common.consumers.service.impl;

import com.inev1te.study.helloworlds.common.consumers.service.FooService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class FooServiceImpl implements FooService {

    @Resource
    private RestTemplate restTemplate;

    @Override
    public String test() {
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:12345/foo/test", HttpMethod.GET, null, String.class);
        return response.getBody();
    }
}
