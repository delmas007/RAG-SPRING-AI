package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceServiceImp implements UserDetailsService {

    public UserDetailServiceServiceImp(UtilisateurServiceImp utilisateurServiceImp) {
        this.utilisateurServiceImp = utilisateurServiceImp;
    }

    UtilisateurServiceImp utilisateurServiceImp;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UtilisateurDto utilisateurDto = utilisateurServiceImp.loadUserByUsername(username);
        if(utilisateurDto==null) throw new UsernameNotFoundException("pas D'utilisateur trouver");
        String authorities = String.valueOf(utilisateurDto.getRole().getRole());
        UserDetails userDetails = User
                .withUsername(utilisateurDto.getId())
                .password(utilisateurDto.getPassword())
                .authorities(authorities)
                .build();
        return userDetails;
    }
}
