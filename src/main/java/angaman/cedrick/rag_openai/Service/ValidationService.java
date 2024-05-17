package angaman.cedrick.rag_openai.Service;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import org.springframework.stereotype.Service;

@Service
public interface ValidationService {

    void enregistrer(UtilisateurDto utilisateurDto);
}
