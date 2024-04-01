package angaman.cedrick.rag_openai.Controller;

import angaman.cedrick.rag_openai.Service.Imp.RagServiceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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



    @PostMapping("/fichier/pdf")
    public ResponseEntity<Void> textEmbeddingsPdf(@RequestParam("files") MultipartFile[] pdfFiles) {
        List<Resource> resources = Arrays.asList(pdfFiles)
                .stream()
                .map(file -> {
                    try {
                        return new ByteArrayResource(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        ragServiceImp.textEmbeddingPdf(((List<?>) resources).toArray(new Resource[0]));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fichier/word")
    public ResponseEntity<Void> textEmbeddingsWord(@RequestParam("files") MultipartFile[] wordFiles) {
        List<Resource> resources = Arrays.stream(wordFiles)
                .map(file -> {
                    try {
                        return new ByteArrayResource(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        ragServiceImp.textEmbeddingWord(resources.toArray(new Resource[0]));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fichier/excel")
    public ResponseEntity<Void> textEmbeddingsExcel(@RequestParam("files") MultipartFile[] excelFiles) {
        List<Resource> resources = Arrays.stream(excelFiles)
                .map(file -> {
                    try {
                        return new ByteArrayResource(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        ragServiceImp.textEmbeddingExcel(resources.toArray(new Resource[0]));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fichier/powerpoint")
    public ResponseEntity<Void> textEmbeddingsPowerpoint(@RequestParam("files") MultipartFile[] powerpointFiles) {
        List<Resource> resources = Arrays.stream(powerpointFiles)
                .map(file -> {
                    try {
                        return new ByteArrayResource(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        ragServiceImp.textEmbeddingPowerpoint(resources.toArray(new Resource[0]));
        return ResponseEntity.ok().build();
    }


//    @PostMapping("/fichier/word")
//    public ResponseEntity<Void> textEmbeddingsWord(@RequestParam("files") Resource[] worldResources) {
//        ragServiceImp.textEmbeddingWord(worldResources);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/fichier/excel")
//    public ResponseEntity<Void> textEmbeddingsExcel(@RequestParam("files") Resource[] excelResources) {
//        ragServiceImp.textEmbeddingExcel(excelResources);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/fichier/powerpoint")
//    public ResponseEntity<Void> textEmbeddingsPowerpoint(@RequestParam("files") Resource[] powerpointlResources) {
//        ragServiceImp.textEmbeddingPowerpoint(powerpointlResources);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/fichier/pdf")
//    public ResponseEntity<Void> textEmbeddingsPdf(@RequestParam("files") Resource[] pdfResources) {
//        ragServiceImp.textEmbeddingPdf(pdfResources);
//        return ResponseEntity.ok().build();
//    }
}
