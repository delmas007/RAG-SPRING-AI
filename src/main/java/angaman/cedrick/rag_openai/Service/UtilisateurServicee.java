package angaman.cedrick.rag_openai.Service;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


public interface UtilisateurServicee {


    UtilisateurDto loadUserByUsername(String username);

}
