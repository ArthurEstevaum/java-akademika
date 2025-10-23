package com.coda_fofos.java_akademika.enums;

/**
 * Enumeração representando o status de uma disciplina para um aluno.
 */
public enum Status {
    /**
     * A disciplina está atualmente em andamento.
     */
    CURSANDO,

    /**
     * O aluno concluiu a disciplina com sucesso (nota suficiente para aprovação).
     */
    APROVADO,

    /**
     * O aluno não atingiu a nota mínima para aprovação na disciplina.
     */
    REPROVADO,

    /**
     * Status inicial antes de iniciar a disciplina, ou quando o status ainda não foi definido.
     */
    PENDENTE,

    /**
     * A matrícula na disciplina foi oficialmente trancada pelo aluno ou instituição.
     */
    TRANCADO

    // Você pode adicionar outros status conforme necessário para o seu sistema, por exemplo:
    // DISPENSADO, // Aluno dispensado de cursar por equivalência ou outro motivo
    // EQUIVALENCIA // Disciplina cumprida por equivalência
}