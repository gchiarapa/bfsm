package br.com.bfsm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MVCController {
	
	private static final Logger log = LoggerFactory.getLogger(MVCController.class);
	
    @GetMapping(value = "/login")
    public String login() {
    	log.info("/login");

    	log.info("Return login");

        //return login.html located in /resources/templates
        return "login";
    }

}
