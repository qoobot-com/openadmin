package com.qoobot.openadmin.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin/hello")
    public String hello() {
        return "Hello Admin";
    }
}

