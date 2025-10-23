package enities; // <- CORRIGIDO: Pacote ajustado para 'enities'

// Importações do Jakarta Persistence
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;     // Importação adicionada
import jakarta.persistence.Enumerated;   // Importação adicionada
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

// Importações do Lombok
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Importação para Set
import java.util.Set;

// Importação CORRIGIDA para o Enum Days
import com.coda_fofos.java_akademika.enums.Days; // <- CORRIGIDO

/**
 * Entidade que representa um dia da semana.
 * Usada para associar disciplinas a dias específicos.
 * Mapeada para a tabela "tb_days".
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_days")
public class Day {

    /**
     * ID único do dia.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O dia da semana específico (ex: SEGUNDA, TERCA).
     * Mapeado a partir do Enum Days.
     */
    @Column(nullable = false, name = "day_of_week")
    @Enumerated(EnumType.STRING) // Mapear Enum como String no DB
    private Days day; // Usa o enum Days importado corretamente

    
    @ManyToMany(mappedBy = "days") 
    private Set<Subject> subjects; // Subject está no mesmo pacote 'enities'

    /**
     * Construtor para criar uma instância de Day com um dia específico.
     * @param day O enum Days representando o dia da semana.
     */
    public Day(Days day) {
        this.day = day;
    }
}