package com.coda_fofos.java_akademika.outputs.quiz;

import java.util.List;


public record Exercise(
    String question,        
    List<Option> options,   
    String correctLabel,    
    String explanation      
) {}