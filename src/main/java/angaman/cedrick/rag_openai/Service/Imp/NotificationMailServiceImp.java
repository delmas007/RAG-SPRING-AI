package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Model.Validation;
import angaman.cedrick.rag_openai.Service.NotificationMailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationMailServiceImp implements NotificationMailService {

    public NotificationMailServiceImp(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    JavaMailSender javaMailSender;
    @Override
    public void sendNotificationMail(Validation validation) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("Angamancedrick@gmail.com");
        mail.setTo(validation.getUtilisateur().getEmail());
        mail.setSubject("Votre code d'activation");
        String message = "Bonjour "+validation.getUtilisateur().getNom()+"\n"+
                "Voici votre code d'activation: "+validation.getCode();
        mail.setText(message);
        javaMailSender.send(mail);

    }
}
