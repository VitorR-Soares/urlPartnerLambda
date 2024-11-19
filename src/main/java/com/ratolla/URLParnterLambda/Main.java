package com.ratolla.URLParnterLambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>>{

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Método responsável por cuidar das requisições HTTP
    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        // Capturar dados do Body da Requisição HTTP
        String body = (String) input.get("body");
        // Desserializar o conteúdo do JSON
        Map<String, String> bodyMap;
        try {
            bodyMap = objectMapper.readValue(body , Map.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("error parsing JSON body: " + exception.getMessage());
        }

        // Acessar dados
        String origjnalUrl = bodyMap.get("originalUrl");
        String expirationTime = bodyMap.get(("expirationTime"));

    String shortUrlCode = UUID.randomUUID().toString().substring(0,8);

    Map<String,String> response = new HashMap();
    response.put("code", shortUrlCode);


        return response;
    }

}