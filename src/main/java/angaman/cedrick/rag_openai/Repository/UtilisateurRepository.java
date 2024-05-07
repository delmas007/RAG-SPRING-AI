package angaman.cedrick.rag_openai.Repository;

import angaman.cedrick.rag_openai.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>{
    Optional<Utilisateur> findByUsername(String username);

}
