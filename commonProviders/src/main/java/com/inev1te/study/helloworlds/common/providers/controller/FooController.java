package com.inev1te.study.helloworlds.common.providers.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/foo")
public class FooController {

    @GetMapping("/test")
    public String test() throws InterruptedException {
        throw new RuntimeException("this is an error");
//        Thread.sleep(60*1000);
//        return "success";
    }
}
