package angaman.cedrick.rag_openai.Service;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;


public interface ValidationService {

    void enregistrer(UtilisateurDto utilisateurDto);

    void resendMail(Utilisateur utilisateur);


}
