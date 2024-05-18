package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Exception.EntityNotFoundException;
import angaman.cedrick.rag_openai.Exception.ErrorCodes;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import angaman.cedrick.rag_openai.Model.Validation;
import angaman.cedrick.rag_openai.Repository.ValidationRepository;
import angaman.cedrick.rag_openai.Service.ValidationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@Service
public class ValidationServiceImp implements ValidationService {

    public ValidationServiceImp(ValidationRepository validationRepository, NotificationMailServiceImp notificationMailServiceImp) {
        this.validationRepository = validationRepository;
        this.notificationMailServiceImp = notificationMailServiceImp;
    }

    private final ValidationRepository validationRepository;
    private final NotificationMailServiceImp notificationMailServiceImp;
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
        public void enregistrer(UtilisateurDto utilisateurDto) {
        Validation validation = new Validation();
        validation.setUtilisateur(UtilisateurDto.toEntity(utilisateurDto));
        Instant creation = Instant.now();
        Instant expiration = creation.plus((Duration.ofMinutes(10)));
        validation.setCreation(creation);
        validation.setExpiration(expiration);

        Random random = new Random();
        int randomCode = random.nextInt(999999);
        String code = String.format("%06d", randomCode);

        validation.setCode(code);
        validationRepository.save(validation);
        notificationMailServiceImp.sendNotificationMail(validation);
    }

    @Override
    public Void activation(String code, String password) {
        Validation leCodeEstInvalide = validationRepository.findByCode(code).orElseThrow(() -> new EntityNotFoundException("Le code est invalide",
                ErrorCodes.CODE_INVALIDE));
        if (Instant.now().isAfter(leCodeEstInvalide.getExpiration())) {
            throw new EntityNotFoundException("Le code a expiré", ErrorCodes.CODE_EXPIRE);
        }
        return null;
    }


//    public void enregistrer(UtilisateurDto utilisateurDto) {
//        Validation validation = new Validation();
//        validation.setUtilisateur(UtilisateurDto.toEntity(utilisateurDto));
//        Instant creation = Instant.now();
//        Instant expiration = creation.plus((Duration.ofMinutes(10)));
//        validation.setCreation(creation);
//        validation.setExpiration(expiration);
//
//        Random random = new Random();
//        int randomCode = random.nextInt(999999);
//        String code = String.format("%06d", randomCode);
//
//        validation.setCode(code);
//        validationRepository.save(validation);
//        notificationMailServiceImp.sendNotificationMail(validation);
//    }
}
