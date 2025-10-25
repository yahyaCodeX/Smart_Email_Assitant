package com.email.writer.service;

import com.email.writer.dtos.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class EmailGeneratorService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiapiurl;
    @Value("${gemini.api.key}")
    private String gemeiniapikey;

    public EmailGeneratorService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String generateEmailReply(EmailRequest emailRequest){

        String prompt=BuildPrompt(emailRequest);

        Map<String,Object> requestBody= Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                }
        );

        String response=webClient.post()
                .uri(geminiapiurl + gemeiniapikey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        
        return extractReponse(response);
    }

    private String extractReponse(String reponse) {
        try {
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(reponse);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            log.error("Error While Processing request{}", String.valueOf(e));
            return  null;
        }
    }

    private String BuildPrompt(EmailRequest emailRequest){
        StringBuilder prompt=new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content.please Don't generate subject line ");
        if(emailRequest.getTone() !=null && !emailRequest.getTone().isEmpty()){
            prompt.append("use a ").append(emailRequest.getTone()).append(" tone ");
        }
        prompt.append("\n Original email:\n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
