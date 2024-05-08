package angaman.cedrick.rag_openai.Service;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurService {

    UtilisateurDto Inscription(UtilisateurDto utilisateur,String role);


    UtilisateurDto loadUserByUsername(String username);

}
