package com.coda_fofos.java_akademika.controllers.IAkademika;

import com.coda_fofos.java_akademika.dtos.PromptRequestDTO;
import com.coda_fofos.java_akademika.outputs.PromptResponse;
import com.coda_fofos.java_akademika.services.IaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromptController {

    private final IaService promptProcessor;

    public PromptController(IaService promptProcessor) {
        this.promptProcessor = promptProcessor;
    }

    @PostMapping("/prompt/process")
    public ResponseEntity<PromptResponse> getPromptResponse(@RequestBody String prompt) {
        PromptResponse promptResponse = promptProcessor.getPromptResponse(prompt);

        return ResponseEntity.ok(promptResponse);
    }
}
