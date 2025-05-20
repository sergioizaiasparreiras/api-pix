package com.livrementehomeopatia.demo.controller;

import com.livrementehomeopatia.demo.dto.PixRequestPayload;
import com.livrementehomeopatia.demo.service.PixService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pix")
public record PixController(PixService pixService) {

    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarChavesPix() {
        return ResponseEntity.ok(pixService.listarChavesPix().toMap());
    }

    @PostMapping("/qrcode")
    public ResponseEntity<?> criarQrCode(@RequestBody PixRequestPayload payload) {
        return ResponseEntity.ok(pixService.criarQrCode(payload).toMap());
    }

    @DeleteMapping
    public ResponseEntity<?> deletarChavePix(@RequestParam String chavePix) {
        return ResponseEntity.ok(pixService.deletarChavePix(chavePix).toMap());
    }
}