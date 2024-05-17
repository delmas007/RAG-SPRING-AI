package angaman.cedrick.rag_openai.Service;

import angaman.cedrick.rag_openai.Model.Validation;
import org.springframework.stereotype.Service;


public interface NotificationMailService {
    void sendNotificationMail(Validation validation);
}
