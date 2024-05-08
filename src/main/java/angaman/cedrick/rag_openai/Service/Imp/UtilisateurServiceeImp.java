package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Exception.EntityNotFoundException;
import angaman.cedrick.rag_openai.Exception.ErrorCodes;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import angaman.cedrick.rag_openai.Repository.UtilisateurrRepository;
import angaman.cedrick.rag_openai.Service.UtilisateurServicee;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UtilisateurServiceeImp implements UtilisateurServicee {

    UtilisateurrRepository utilisateurrRepository;

    public UtilisateurServiceeImp(UtilisateurrRepository utilisateurrRepository) {
        this.utilisateurrRepository = utilisateurrRepository;
    }



    @Override
    public UtilisateurDto loadUserByUsername(String username) {
        Optional<Utilisateur> user = utilisateurrRepository.findByUsername(username);
        return UtilisateurDto.fromEntity(user.orElseThrow(()-> new EntityNotFoundException("Utilisateur pas trouver ",
                ErrorCodes.UTILISATEUR_PAS_TROUVER)));
    }

}
