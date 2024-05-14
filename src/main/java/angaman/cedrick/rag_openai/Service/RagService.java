package angaman.cedrick.rag_openai.Service;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Model.Utilisateur;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.stringtemplate.v4.ST;

public interface RagService {

    String askLlm(String query);

    void textEmbeddingPdf(Resource[] pdfResources, UtilisateurDto utilisateur);


    void textEmbeddingWord(Resource[] worldResources);

    void textEmbeddingExcel(Resource[] excelResources);

    void textEmbeddingPowerpoint(Resource[] PowerpointResources);

    String Image(String prompt);

}
