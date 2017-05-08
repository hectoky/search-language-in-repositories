package com.example.github.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.github.service.GithubRepositoryService;

@Controller
@RequestMapping("/language")
public class GithubRepositoryController {

    private Logger log = Logger.getLogger(GithubRepositoryController.class);

    @Autowired
    GithubRepositoryService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> getLanguage(@RequestParam(value = "user", required = true) String user) {
        log.info("Resquest received, sending response...");
        try {
            String language = service.getProgrammingLanguage(service.searchRepositoryByUser(user));
            log.info("The language is " + language);
            return new ResponseEntity<String>(language, HttpStatus.OK);
        } catch (Exception e) {
            String message = String.format("Error in the request: %s", e.getMessage());
            log.error(message, e);
            return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
        }

    }
}
