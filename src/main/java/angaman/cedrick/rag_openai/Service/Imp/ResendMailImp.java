package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Model.Utilisateur;
import angaman.cedrick.rag_openai.Repository.UtilisateurRepository;
import angaman.cedrick.rag_openai.Service.ResendMail;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ResendMailImp implements ResendMail {

    UtilisateurRepository utilisateurRepository;
    ValidationServiceImp validationServiceImp;

    @Override
    public int resendMail(String email) {
        Optional<Utilisateur> byEmail = utilisateurRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            Utilisateur user = byEmail.get();
            validationServiceImp.resendMail(user);
            return 1;
        }
        return 0;
    }
}
