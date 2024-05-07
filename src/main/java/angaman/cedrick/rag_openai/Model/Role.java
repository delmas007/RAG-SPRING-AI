package angaman.cedrick.rag_openai.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class Role {

    @Id
    @Column(name = "nom")
    @Enumerated(EnumType.STRING)
    private Roles role;

    @OneToMany(mappedBy = "role")
    private List<Utilisateur> utilisateur;




}
