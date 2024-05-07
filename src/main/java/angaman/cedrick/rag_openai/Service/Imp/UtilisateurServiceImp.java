package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Exception.EntityNotFoundException;
import angaman.cedrick.rag_openai.Exception.ErrorCodes;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import angaman.cedrick.rag_openai.Repository.UtilisateurRepository;
import angaman.cedrick.rag_openai.Service.UtilisateurService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilisateurServiceImp implements UtilisateurService {

    public UtilisateurServiceImp(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    UtilisateurRepository utilisateurRepository;

    @Override
    public UtilisateurDto Inscription(Utilisateur utilisateur) {
        UtilisateurDto user = loadUserByUsername(utilisateur.getUsername());
        if (user == null){
            return UtilisateurDto.fromEntity(utilisateurRepository.save(utilisateur));
        }
        else{
            throw new EntityNotFoundException("Utilisateur existe deja", ErrorCodes.UTILISATEUR_DEJA_EXIST);

        }
    }

    @Override
    public UtilisateurDto loadUserByUsername(String username) {
        Optional<Utilisateur> user = utilisateurRepository.findByUsername(username);
        return UtilisateurDto.fromEntity(user.orElseThrow(null));
    }
}
