package com.livrementehomeopatia.demo_pix.controller;


import com.livrementehomeopatia.demo_pix.service.PixService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/pix", produces = MediaType.APPLICATION_JSON_VALUE)
public record PixController(PixService pixService){

    @RequestMapping("/listar")
    public ResponseEntity<String> listPixKeys(){
        var response = this.listPixKeys();
        return ResponseEntity.ok().body(response.toString());
    }
}
