package com.email.writer.service;


import com.email.writer.model.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService
{

        private final WebClient webClient;

        @Value("${gemini.api.url}")
        private String geminiApiUrl;
        @Value("${gemini.api.key}")
        private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailRequest)
        {
             String prompt=buildPrompt(emailRequest);

             Map<String,Object> resquestBody = Map.of(
                     "contents",new Object[]{
                             Map.of(
                                     "parts",new Object[]
                                             {
                                                    Map.of(
                                                            "text", prompt
                                                    )
                                             }
                             )
                     });

             // Call the Gemini API to generate the email reply
                String response = webClient.post()
                        .uri(geminiApiUrl + geminiApiKey)
                        .header("Content-Type", "application/json")
                        .bodyValue(resquestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                //response processing
            return extractResponseContent(response);
        }

    private String extractResponseContent(String response)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        }
        catch (Exception e)
        {
                return "Error processing response: " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest)
    {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional eamil reply for the following email content.Please don't generate a subject line. ");
        if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty())
        {
            prompt.append("The tone of the email should be: ").append(emailRequest.getTone()).append(". ");
        }

        prompt.append("\n Original Email : \n").append(emailRequest.getEmailContent());

        return prompt.toString();
    }
}
