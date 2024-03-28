package angaman.cedrick.rag_openai;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootApplication
@PropertySource("classpath:Key/.env")
public class RagOpenAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagOpenAiApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner (VectorStore vectorStore, JdbcTemplate jdbcTemplate,@Value("classpath:MEMOIRE_PFE_ANGAMAN_BROU_CEDRICK_DELMAS.pdf") Resource resource) {
        return args -> {
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.defaultConfig();
            PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource,config);
            List<Document> documents = pagePdfDocumentReader.get();
            System.out.println(documents.size());

        };
    }

}
