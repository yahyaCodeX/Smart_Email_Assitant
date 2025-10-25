package com.email.writer.service;

import com.email.writer.dtos.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailGeneratorService {
    @Value("${gemini.api.url}")
    private String geminiapiurl;
    @Value("${gemini.api.key}")
    private String gemeiniapikey;
    public String generateEmailReply(EmailRequest emailRequest){
        String prompt=BuildPrompt(emailRequest);
        Map<String,Object> requestBody= Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                }
        );

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
