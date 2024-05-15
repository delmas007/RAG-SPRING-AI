package angaman.cedrick.rag_openai;

import angaman.cedrick.rag_openai.Config.RsakeysConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(RsakeysConfig.class)
@PropertySource("classpath:Key/.env")
public class RagOpenAiApplication {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public static void main(String[] args) {
        SpringApplication.run(RagOpenAiApplication.class, args);
    }


//    VectorRepository vectorRepository;
//
//    public RagOpenAiApplication(VectorRepository vectorRepository) {
//        this.vectorRepository = vectorRepository;
//    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//        @Bean
//    CommandLineRunner commandLineRunner () {
//        return args -> {
//
////            Optional<String> utilisateur = vectorRepository.findUserIdByVectorStoreId("248fb385-694e-41aa-a302-e438e6fbc523");
////            List<Document> all = documentRepository.findAll();
////            List<String> vectorStoreById = vectorRepository.findVectorStoreById("19155c5f-a7bd-4c19-ab84-800c036de854");
////            if (all != null) {
////                System.out.println(all);
////            } else {
////                // Gérer le cas où aucun utilisateur n'est trouvé
////                System.out.println("Aucun utilisateur trouvé dans la base de données.");
////            }
//
//
//        };
//    }

}