package com.coda_fofos.java_akademika.controllers.IAkademika;

import com.coda_fofos.java_akademika.outputs.Summary.Summary;
import com.coda_fofos.java_akademika.services.IaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SummaryController {
    private final IaService summaryGenerator;

    public SummaryController(IaService summaryGenerator) {
        this.summaryGenerator = summaryGenerator;
    }

    @GetMapping("/summary/generate")
    public ResponseEntity<Summary> getSummary(@RequestParam String topic, @RequestParam String size)  {
        Summary summary = summaryGenerator.generateSummary(topic, size);

        return ResponseEntity.ok(summary);
    }
}
