package com.email.writer.controller;


import com.email.writer.model.EmailRequest;
import com.email.writer.service.EmailGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*") // Allow all origins for simplicity, adjust as needed
public class EmailGeneratorController
{




          EmailGeneratorService emailGeneratorService;


              @PostMapping("/generate")
              public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest)
              {
                        // Call the service to generate the email reply
                        String response=  emailGeneratorService.generateEmailReply(emailRequest);
                        return  ResponseEntity.ok(response);
              }

         public EmailGeneratorController(EmailGeneratorService emailGeneratorService)
         {
                     this.emailGeneratorService = emailGeneratorService;
         }

}
