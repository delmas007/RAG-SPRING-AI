package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Model.Validation;
import angaman.cedrick.rag_openai.Repository.ValidationRepository;
import angaman.cedrick.rag_openai.Service.ValidationService;
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

    @Override
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
}
