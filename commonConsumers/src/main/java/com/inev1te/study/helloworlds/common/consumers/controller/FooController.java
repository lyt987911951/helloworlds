package com.inev1te.study.helloworlds.common.consumers.controller;

import com.inev1te.study.helloworlds.common.consumers.service.FooService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/consumer/foo")
public class FooController {

    @Resource
    private FooService fooService;

    @GetMapping("test")
    public String test(){
        return fooService.test();
    }
}
