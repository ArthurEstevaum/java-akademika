package enities;

// Importações JPA e outras necessárias
import jakarta.persistence.*;
// import jakarta.validation.constraints.Email; // <- REMOVIDO
// import jakarta.validation.constraints.NotNull; // <- REMOVIDO
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.Set;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "tb_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false, unique = true)
    
    private String email;

    @OneToMany(mappedBy = "user")
    @Cascade(CascadeType.ALL)
    private Set<Subject> subjects;

    public User(String username, String encodedPassword, String email) {
        this.username = username;
        this.password = encodedPassword;
        this.email = email;
    }
}