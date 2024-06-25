package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Exception.EntityNotFoundException;
import angaman.cedrick.rag_openai.Exception.ErrorCodes;
import angaman.cedrick.rag_openai.Model.Jwt;
import angaman.cedrick.rag_openai.Model.Role;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import angaman.cedrick.rag_openai.Model.Validation;
import angaman.cedrick.rag_openai.Repository.JwtRepository;
import angaman.cedrick.rag_openai.Repository.UtilisateurRepository;
import angaman.cedrick.rag_openai.Repository.ValidationRepository;
import angaman.cedrick.rag_openai.Service.UtilisateurService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UtilisateurServiceImp implements UtilisateurService {

    UtilisateurRepository utilisateurRepository;

    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtEncoder jwtEncoder;
    ValidationRepository validationRepository;
    JwtRepository jwtRepository;

    public UtilisateurServiceImp(ValidationRepository validationRepository,UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, ValidationServiceImp validationServiceImp, JwtRepository jwtRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.validationServiceImp = validationServiceImp;
        this.validationRepository = validationRepository;
        this.jwtRepository = jwtRepository;
    }

    ValidationServiceImp validationServiceImp;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UtilisateurDto Inscription(UtilisateurDto utilisateur, String role) {
        Utilisateur existingUser = utilisateurRepository.findByUsername(utilisateur.getUsername()).orElse(null);
        if (existingUser == null) {
            Utilisateur newUser = Utilisateur.builder()
                    .id(UUID.randomUUID().toString())
                    .username(utilisateur.getUsername())
                    .password(passwordEncoder.encode(utilisateur.getPassword()))
                    .nom(utilisateur.getNom())
                    .prenom(utilisateur.getPrenom())
                    .email(utilisateur.getEmail())
                    .actif(false)
                    .role(Role.builder().role(role.toUpperCase()).build())
                    .build();
            Utilisateur savedUser = utilisateurRepository.save(newUser);
            UtilisateurDto utilisateurDto = UtilisateurDto.fromEntity(savedUser);
            entityManager.close();
            validationServiceImp.enregistrer(utilisateurDto);
            return utilisateurDto;
        } else {
            throw new EntityNotFoundException("Utilisateur existe deja", ErrorCodes.UTILISATEUR_DEJA_EXIST);
        }
    }

    @Override
    public int activation(String code) {
        Validation leCodeEstInvalide = validationRepository.findByCode(code).orElseThrow(() -> new EntityNotFoundException("Le code est invalide",
                ErrorCodes.CODE_INVALIDE));
        if (Instant.now().isAfter(leCodeEstInvalide.getExpiration())) {
            throw new EntityNotFoundException("Le code a expiré", ErrorCodes.CODE_EXPIRE);
        }
        Utilisateur utilisateurActiver = utilisateurRepository.findById(leCodeEstInvalide.getUtilisateur().getId()).orElseThrow(() -> new EntityNotFoundException("Utilisateur pas trouver",
                ErrorCodes.UTILISATEUR_PAS_TROUVER));
        utilisateurActiver.setActif(true);
        utilisateurRepository.save(utilisateurActiver);
        return 1;
    }

    @Override
    public UtilisateurDto loadUserByUsername(String username) {
        Optional<Utilisateur> user = utilisateurRepository.findByUsername(username);
        return UtilisateurDto.fromEntity(user.orElseThrow(()-> new EntityNotFoundException("Utilisateur inexistant ",
                ErrorCodes.UTILISATEUR_PAS_TROUVER)));
    }

    public ResponseEntity<Map<String, String>> Connexion(String username, String password) {
        String subject=null;
        String scope=null;
        UtilisateurDto utilisateur = loadUserByUsername(username);

        if (!utilisateur.getActif()) {
            throw new EntityNotFoundException("Utilisateur non actif", ErrorCodes.UTILISATEUR_NON_ACTIF);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        subject=utilisateur.getUsername();
        String nom = loadUserByUsername(username).getNom();
        String id = loadUserByUsername(username).getId();
        String prenom = loadUserByUsername(username).getPrenom();
        scope=authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));


        Map<String, String> idToken=new HashMap<>();
        Instant instant=Instant.now();
        JwtClaimsSet jwtClaimsSet=JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(instant)
                .expiresAt(instant.plus((Duration.ofMinutes(10))))
                .issuer("security-service")
                .claim("scope",scope)
                .claim("nom",nom)
                .claim("id",id)
                .claim("prenom",prenom)
                .build();
        String jwtAccessToken=jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        idToken.put("accessToken",jwtAccessToken);
        final Jwt jwt = Jwt.builder()
                .value(jwtAccessToken)
                .desactive(false)
                .expire(false)
                .utilisateur(UtilisateurDto.toEntity(utilisateur))
                .build();
        this.jwtRepository.save(jwt);

        return new ResponseEntity<>(idToken, HttpStatus.OK);
    }
}
