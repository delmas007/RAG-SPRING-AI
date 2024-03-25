package angaman.cedrick.rag_openai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:Key/.env")
public class RagOpenAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagOpenAiApplication.class, args);
    }

}
