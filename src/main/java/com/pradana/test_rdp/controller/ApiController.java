package com.pradana.test_rdp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pradana.test_rdp.service.ApiService;

@RestController
public class ApiController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/search")
    public ResponseEntity<?> getSearch(@RequestParam(name = "q") String param) {
        return apiService.getSearch(param);
    }

}
