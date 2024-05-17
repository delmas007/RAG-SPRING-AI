package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Exception.EntityNotFoundException;
import angaman.cedrick.rag_openai.Exception.ErrorCodes;
import angaman.cedrick.rag_openai.Model.Role;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import angaman.cedrick.rag_openai.Repository.UtilisateurRepository;
import angaman.cedrick.rag_openai.Service.UtilisateurService;
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

    public UtilisateurServiceImp(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager,JwtEncoder jwtEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtEncoder jwtEncoder;
    ValidationServiceImp validationServiceImp;

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
            UtilisateurDto utilisateurDto = UtilisateurDto.fromEntity(utilisateurRepository.save(UtilisateurDto.toEntity(userDto)));
            validationServiceImp.enregistrer(userDto);
            return utilisateurDto;
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

    public ResponseEntity<Map<String, String>> Connexion(String username, String password) {
        String subject=null;
        String scope=null;
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        subject=loadUserByUsername(username).getUsername();
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

        return new ResponseEntity<>(idToken, HttpStatus.OK);
    }
}
