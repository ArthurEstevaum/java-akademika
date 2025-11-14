package com.coda_fofos.java_akademika.outputs.quiz;

import java.util.List;


public record Quiz(
    String topic,              
    List<Exercise> exercises   
) {}