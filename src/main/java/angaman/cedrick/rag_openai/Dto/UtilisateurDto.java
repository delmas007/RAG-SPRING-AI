package angaman.cedrick.rag_openai.Dto;

import angaman.cedrick.rag_openai.Model.Role;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UtilisateurDto {
    private String id;
    private String username;
    private String password;
    private String nom;
    private String prenom;
    private String email;
    private Role role;

    public static UtilisateurDto fromEntity(Utilisateur utilisateur){
        if (utilisateur == null){
            return null;
        }
        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .username(utilisateur.getUsername())
                .password(utilisateur.getPassword())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .build();
    }

    public static Utilisateur toEntity(UtilisateurDto utilisateurDto){
        if (utilisateurDto == null){
            return null;
        }
        return Utilisateur.builder()
                .id(utilisateurDto.getId())
                .username(utilisateurDto.getUsername())
                .password(utilisateurDto.getPassword())
                .nom(utilisateurDto.getNom())
                .prenom(utilisateurDto.getPrenom())
                .email(utilisateurDto.getEmail())
                .role(utilisateurDto.getRole())
                .build();
    }
}
