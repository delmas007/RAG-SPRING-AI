package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Model.Validation;
import angaman.cedrick.rag_openai.Service.ValidationService;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Random;

public class ValidationServiceImp implements ValidationService {

    public ValidationServiceImp(ValidationService validationService) {
        this.validationService = validationService;
    }

    private ValidationService validationService;
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


    }
}
