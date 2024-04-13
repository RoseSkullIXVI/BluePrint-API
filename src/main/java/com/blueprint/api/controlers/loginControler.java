package com.blueprint.api.controlers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class loginControler {
    @GetMapping("/index")
    public String getMethodName() {
        return "index";
    }
    
    
}
