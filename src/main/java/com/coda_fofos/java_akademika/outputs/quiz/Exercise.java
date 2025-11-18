package com.coda_fofos.java_akademika.outputs.quiz;

import com.coda_fofos.java_akademika.enums.OptionLetter;

import java.util.HashMap;
import java.util.List;

public record Exercise(String questionName, List<Option> options, OptionLetter correctAnswerLetter, HashMap<OptionLetter, String> explanations) {
}