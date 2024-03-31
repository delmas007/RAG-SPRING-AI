package angaman.cedrick.rag_openai.Service;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

public interface RagService {

    String askLlm(String query);

    void textEmbeddingPdf(Resource[] pdfResources);


    void textEmbeddingWord(Resource[] worldResources);

    void textEmbeddingExcel(Resource[] excelResources);

    void textEmbeddingPowerpoint(Resource[] PowerpointResources);

}
