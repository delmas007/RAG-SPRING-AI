package angaman.cedrick.rag_openai.Service;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;


public interface UtilisateurService {

    UtilisateurDto Inscription(UtilisateurDto utilisateur,String role);

    int activation(String code);
    UtilisateurDto loadUserByUsername(String username);

}
