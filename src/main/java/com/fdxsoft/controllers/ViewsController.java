package com.fdxsoft.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewsController {

    @GetMapping("/wysiwyg")
    public String getWYSIWYG() {
        return "wysiwyg";
    }
    
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
    
    @GetMapping("/templates-list")
    public String templatesList() {
        return "templates-list";
    }
}
