package com.livrementehomeopatia.demo_pix.service;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import com.livrementehomeopatia.demo_pix.pix.PixConfig;


import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class PixService {

    private final JSONObject configuration;

    public PixService(final PixConfig pixConfig){
        this.configuration = new JSONObject();
        this.configuration.put("client_id", pixConfig.clientId());
        this.configuration.put("client_secret", pixConfig.clientSecret());
        this.configuration.put("certificate", pixConfig.certificatePath());
        this.configuration.put("sand_box", pixConfig.sandbox());
        this.configuration.put("debug", pixConfig.debug());
    }

    public JSONObject listPixKey(){
        return runOperation("pixListEvp", new HashMap<>());
    }

    private JSONObject runOperation(String operation, HashMap<String, String> params) {
        final var returned = new JSONObject();

        try {
            EfiPay efi = new EfiPay(configuration);
            JSONObject response = efi.call(operation, params, new JSONObject());
            log.info("Result: {}", response);
        } catch (EfiPayException e) {
            log.error(e.getError());
            returned.put("error", e.getErrorDescription());
        } catch (UnsupportedOperationException | JSONException operationException) {
            log.warn("Invalid JSON format {}", operationException.getMessage());
        } catch (Exception e) {
            returned.put("error", "Unable to complete operation ");
        }
        return returned;
    }
}
