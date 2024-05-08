package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Exception.EntityNotFoundException;
import angaman.cedrick.rag_openai.Exception.ErrorCodes;
import angaman.cedrick.rag_openai.Model.Role;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import angaman.cedrick.rag_openai.Repository.UtilisateurRepository;
import angaman.cedrick.rag_openai.Service.UtilisateurService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UtilisateurServiceImp implements UtilisateurService {

    UtilisateurRepository utilisateurRepository;

    public UtilisateurServiceImp(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    PasswordEncoder passwordEncoder;

    @Override
    public UtilisateurDto Inscription(UtilisateurDto utilisateur, String role) {
        Utilisateur user = utilisateurRepository.findByUsername(utilisateur.getUsername()).orElse(null);
        if (user == null){
            UtilisateurDto userDto = UtilisateurDto.builder()
                    .id(UUID.randomUUID().toString())
                    .username(utilisateur.getUsername())
                    .password(passwordEncoder.encode(utilisateur.getPassword()))
                    .nom(utilisateur.getNom())
                    .prenom(utilisateur.getPrenom())
                    .email(utilisateur.getEmail())
                    .role(Role.builder().role(role.toUpperCase()).build())
                    .build();
            return UtilisateurDto.fromEntity(utilisateurRepository.save(UtilisateurDto.toEntity(userDto)));
        }
        else{
            throw new EntityNotFoundException("Utilisateur existe deja", ErrorCodes.UTILISATEUR_DEJA_EXIST);

        }
    }

    @Override
    public UtilisateurDto loadUserByUsername(String username) {
        Optional<Utilisateur> user = utilisateurRepository.findByUsername(username);
        return UtilisateurDto.fromEntity(user.orElseThrow(()-> new EntityNotFoundException("Utilisateur pas trouver ",
                ErrorCodes.UTILISATEUR_PAS_TROUVER)));
    }
}
