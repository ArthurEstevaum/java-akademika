package com.coda_fofos.java_akademika.controllers.IAkademika;

import com.coda_fofos.java_akademika.enums.Difficulty;
import com.coda_fofos.java_akademika.outputs.quiz.Quiz;
import com.coda_fofos.java_akademika.services.IaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExerciseController {

    private final IaService quizGenerator;

    public ExerciseController(IaService quizGenerator) {
        this.quizGenerator = quizGenerator;
    }

    @GetMapping("/exercise/generate")
    public ResponseEntity<Quiz> getExercise(@RequestParam(value = "topic", defaultValue = "Primeira guerra mundial") String topic, @RequestParam(value = "difficulty", defaultValue = "MEDIUM") Difficulty difficulty) {
        Quiz quiz = quizGenerator.generateQuiz(topic, difficulty);

        return ResponseEntity.ok(quiz);
    }
}