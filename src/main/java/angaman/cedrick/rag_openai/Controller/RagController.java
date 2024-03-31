package angaman.cedrick.rag_openai.Controller;

import angaman.cedrick.rag_openai.Service.Imp.RagServiceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RagController {

    public RagController(RagServiceImp ragServiceImp) {
        this.ragServiceImp = ragServiceImp;
    }

    RagServiceImp ragServiceImp;

    @GetMapping("/rag")
    public String rag(@RequestParam(name = "query") String query){
        return ragServiceImp.askLlm(query);
    }

    @GetMapping("/ragJson")
    public Map ragJson(@RequestParam(name = "query") String query) throws JsonProcessingException {
        String reponse = ragServiceImp.askLlm(query);
        return new ObjectMapper().readValue(reponse, Map.class);
    }

    @PostMapping("/fichier")
    public Void textEmbeddings(Resource[] pdfResources){
        ragServiceImp.textEmbedding(pdfResources);
        return null;
    }
}
