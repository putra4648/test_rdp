package com.pradana.test_rdp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {
    @GetMapping("/")
    public ModelAndView indexPage() {
        return new ModelAndView("index.html");
    }

}
