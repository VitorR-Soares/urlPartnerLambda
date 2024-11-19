package com.ratolla.URLParnterLambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>>{

    // Dependência responsável por transformar os dados JSON em um Map
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Depedência de Cliente da S3
    private final S3Client s3Client = S3Client.builder().build();

    // Metodo responsável por acessar os dados do Body e por salvá-los no Bucket da S3
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

        // Acessar dados do Map das chaves correspondentes
        String origjnalUrl = bodyMap.get("originalUrl");
        String expirationTime = bodyMap.get(("expirationTime"));
        // Conversão de TimeStamp para segundos
        long expirationTimeInSeconds = Long.parseLong(expirationTime);

        // Criação de um UUID para identificar cada URL
        String shortUrlCode = UUID.randomUUID().toString().substring(0,8);

        // Criação de Objeto de URL
        UrlData urlData = new UrlData(origjnalUrl, expirationTimeInSeconds);

        // Conexão com a S3 e Inserção dos dados no Bucket
        try{
            String urlDataJson = objectMapper.writeValueAsString(urlData);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket("bucket-urlshortener-storage")
                    .key(shortUrlCode + ".json")
                    .build();
            s3Client.putObject(request, RequestBody.fromString(urlDataJson));
        } catch (Exception ex){
            throw  new RuntimeException(("Erro ao salvar dado no bucket" + ex.getMessage()));
        }

        Map<String,String> response = new HashMap();
        response.put("code", shortUrlCode);

        return response;
    }

}