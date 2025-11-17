package enities;

import com.coda_fofos.java_akademika.enums.ActivityStatus;
import com.coda_fofos.java_akademika.enums.Priorities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tb_activities")
@NoArgsConstructor
@Getter
@Setter
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date deadline;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priorities priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;
}
