package com.livrementehomeopatia.demo.service;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import com.livrementehomeopatia.demo.dto.PixRequestPayload;
import com.livrementehomeopatia.demo.pix.PixConfig;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PixService {
    private final PixConfig pixConfig;
    private final boolean sandbox;

    public PixService(PixConfig pixConfig) {
        this.pixConfig = pixConfig;
        this.sandbox = pixConfig.sandbox();
    }

    private EfiPay initializeEfiPay(PixConfig config) {
        try {
            JSONObject options = new JSONObject();
            options.put("client_id", config.clientId());
            options.put("client_secret", config.clientSecret());
            options.put("certificate", resolveCertificatePath(config.certificatePath()));
            options.put("certificate_password", config.certificatePassword()); // <-- LINHA ADICIONADA
            options.put("sandbox", config.sandbox());
            options.put("debug", config.debug());

            log.info("Configuração EFI carregada | Sandbox: {}", config.sandbox());
            return new EfiPay(options);
        } catch (Exception e) {
            log.error("Falha na inicialização do EfiPay", e);
            throw new RuntimeException("Falha na configuração da API PIX", e);
        }
    }

    private String resolveCertificatePath(String path) throws Exception {
        Path certPath = Paths.get(path.replace("\\", "/"));
        if (!Files.exists(certPath)) {
            throw new IllegalArgumentException("Certificado não encontrado: " + certPath.toAbsolutePath());
        }
        return certPath.toString();
    }

    private JSONObject callApiSafely(String endpoint, Map<String, String> params, JSONObject body) {
        try {
            EfiPay efi = initializeEfiPay(pixConfig); // Cria uma nova instância a cada chamada!
            log.debug("Executando {} com params: {}", endpoint, params);
            JSONObject response = efi.call(endpoint, params, body);
            log.debug("Resposta {}: {}", endpoint, response);
            return response;
        } catch (EfiPayException e) {
            log.error("Erro EFI [{}] | Código: {} | Mensagem: {}",
                    endpoint, e.getCode(), e.getErrorDescription());
            return buildErrorResponse(e.getErrorDescription(), String.valueOf(e.getCode()));
        } catch (Exception e) {
            log.error("Erro inesperado em {}: {}", endpoint, e.getMessage());
            return buildErrorResponse("Falha temporária no serviço", "INTERNAL_ERROR");
        }
    }

    public JSONObject listarChavesPix() {
        return callApiSafely("pixListEvp", Collections.emptyMap(), new JSONObject());
    }

    public JSONObject criarChavePix() {
        return callApiSafely("pixCreateEvp", Collections.emptyMap(), new JSONObject());
    }

    public JSONObject deletarChavePix(String chavePix) {
        Map<String, String> params = new HashMap<>();
        params.put("chave", chavePix);
        return callApiSafely("pixDeleteEvp", params, new JSONObject());
    }

    public JSONObject criarQrCode(PixRequestPayload payload) {
        try {
            JSONObject body = new JSONObject()
                    .put("calendario", new JSONObject().put("expiracao", 3600))
                    .put("devedor", new JSONObject()
                            .put("cpf", "12345678909")
                            .put("nome", "Feltex Silva"))
                    .put("valor", new JSONObject().put("original", payload.valor()))
                    .put("chave", payload.chave())
                    .put("infoAdicionais", new JSONArray()
                            .put(new JSONObject().put("nome", "Campo 1").put("valor", "Info 1"))
                            .put(new JSONObject().put("nome", "Campo 2").put("valor", "Info 2")));

            return callApiSafely("pixCreateImmediateCharge", Collections.emptyMap(), body);
        } catch (Exception e) {
            log.error("Erro ao construir payload PIX", e);
            return buildErrorResponse("Erro na criação do QR Code", "INVALID_PAYLOAD");
        }
    }

    private JSONObject buildErrorResponse(String message, String code) {
        return new JSONObject()
                .put("status", "error")
                .put("code", code)
                .put("message", message)
                .put("sandbox", sandbox)
                .put("timestamp", System.currentTimeMillis());
    }
}