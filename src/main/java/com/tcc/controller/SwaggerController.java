package com.tcc.controller;

import com.tcc.config.SwaggerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

/**
 * @author lgdamy@raiadrogasil.com on 14/03/2021
 */
@RestController
public class SwaggerController {

    @Autowired
    private SwaggerConfig config;

    @GetMapping("/swagger/{serviceName}")
    public String getService(@PathVariable("serviceName") String serviceName, HttpServletRequest request) {
        return config.getDefinition(serviceName, request);
    }
}
