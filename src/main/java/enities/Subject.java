package enities; // Pacote da entidade Subject (correto conforme a pasta)

// Importações do Jakarta Persistence
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType; // Importação adicionada para EnumType
import jakarta.persistence.Enumerated; // Importação adicionada para @Enumerated
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// Importações do Lombok
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Importações do Hibernate para @Cascade
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

// Importação para Set
import java.util.Set;

// !!! IMPORTANTE: Importações CORRIGIDAS para seus Enums !!!
// Assumindo que seus enums estão no pacote com.coda_fofos.java_akademika.enums
// Se estiverem em outro lugar, ajuste os imports abaixo.
import com.coda_fofos.java_akademika.enums.Status; // <- CORRIGIDO

/**
 * Entidade que representa uma disciplina (matéria) acadêmica.
 * Mapeada para a tabela "tb_subjects" no banco de dados.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Mapear Enum como String
    private Status status; // Usa o Status importado

    @Column(nullable = true)
    private Short quarter;

    @Column(nullable = true)
    private String teacher;

    @Column(nullable = true, length = 4096)
    private String syllabus;

    @ManyToMany
    @JoinTable(
        name = "days_subjects",
        joinColumns = @JoinColumn(name = "subject_id"),
        inverseJoinColumns = @JoinColumn(name = "day_id")
    )
    private Set<Day> days; // Day deve estar no pacote 'enities' ou importado

    @OneToMany(mappedBy = "subject")
    @Cascade(CascadeType.ALL)
    private Set<Deadline> deadlines; // Deadline deve estar no pacote 'enities' ou importado

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User deve estar no pacote 'enities' ou importado

    /**
     * Construtor para criar uma nova disciplina.
     */
    public Subject(User user, String name, Status status, Short quarter, String teacher, String syllabus) {
        this.user = user;
        this.name = name;
        this.status = status;
        this.quarter = quarter;
        this.teacher = teacher;
        this.syllabus = syllabus;
    }
}