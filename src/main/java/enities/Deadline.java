package enities; 

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Importações do Lombok
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@NoArgsConstructor // Gera construtor padrão
@Getter // Gera getters
@Setter // Gera setters
@Entity // Marca como entidade JPA
@Table(name = "tb_deadlines") // Nome da tabela
public class Deadline {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome ou descrição do prazo. Ex: "Prova 1", "Entrega Trabalho X".
     */
    @Column(name = "name") // Mapeia explicitamente para a coluna 'name'
    private String name;

    /**
     * Data limite para o prazo.
     */
    private LocalDate date;

    /**
     * Disciplina à qual este prazo pertence.
     * Relacionamento Muitos-para-Um (Muitos Deadlines pertencem a Uma Subject).
     */
    @ManyToOne // Define o lado "muitos" do relacionamento
    @JoinColumn(name = "subject_id", nullable = false) // Coluna de chave estrangeira (não pode ser nula)
    private Subject subject; // Subject deve estar no pacote 'enities' ou ser importada

    /**
     * Construtor para criar um novo prazo.
     *
     * @param name    Nome/descrição do prazo.
     * @param date    Data do prazo.
     * @param subject A disciplina associada.
     */
    public Deadline(String name, LocalDate date, Subject subject) {
        this.name = name;
        this.date = date;
        this.subject = subject;
    }
}