package angaman.cedrick.rag_openai.Service;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import org.springframework.stereotype.Repository;


public interface UtilisateurServicee {


    UtilisateurDto loadUserByUsername(String username);

}
