package angaman.cedrick.rag_openai.Controller;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Service.Imp.RagServiceImp;
import angaman.cedrick.rag_openai.Service.Imp.UtilisateurServiceImp;
import angaman.cedrick.rag_openai.Service.Imp.ValidationServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class RagController {

    ValidationServiceImp validationServiceImp;
    RagServiceImp ragServiceImp;
    UtilisateurServiceImp utilisateurServiceImp;
    AuthenticationManager authenticationManager;

//    @GetMapping("/rag/")
//    public ResponseEntity<String> rag(@RequestParam(name = "query") String query) {
//        String response = ragServiceImp.askLlm(query);
//        return ResponseEntity.ok(response);
//    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/inscription/")
    public UtilisateurDto save(@RequestBody UtilisateurDto dto) {
        System.out.println("Request received: " + dto);
        String role = "USER";
        return utilisateurServiceImp.Inscription(dto,role);
    }

    @PostMapping("/connexion")
    public ResponseEntity<Map<String, String>> Connexion(@RequestBody Map<String, String> authentification) {
        String username = authentification.get("username");
        String password = authentification.get("password");

        return utilisateurServiceImp.Connexion(username, password);
    }

    @PostMapping("/activation/")
    public int activation(@RequestBody Map<String, String> codes) {
        System.out.println(codes);
        String code = codes.get("code");

        return utilisateurServiceImp.activation(code);
    }


    @PostMapping("/rag/")
    public ResponseEntity<Map<String, Object>> rag(@RequestParam(name = "query") String query,
                                                   @RequestBody UtilisateurDto utilisateurDto) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", ragServiceImp.askLlm(query,utilisateurDto));
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/ragJson")
//    public Map ragJson(@RequestParam(name = "query") String query) throws JsonProcessingException {
//        String reponse = ragServiceImp.askLlm(query);
//        return new ObjectMapper().readValue(reponse, Map.class);
//    }



    @PostMapping(value = "/fichier/.pdf")
    public ResponseEntity<Void> textEmbeddingsPdf(
            @RequestParam("files") MultipartFile[] pdfFiles,
            @RequestPart("user") UtilisateurDto utilisateurDto) {
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
        ragServiceImp.textEmbeddingPdf(resources.toArray(new Resource[0]), utilisateurDto);
        return ResponseEntity.ok().build();
    }




    @PostMapping("/fichier/.docx")
        public ResponseEntity<Void> textEmbeddingsWord(@RequestParam("files") MultipartFile[] wordFiles,
                                                       @RequestPart("user") UtilisateurDto utilisateurDto) {
            if (wordFiles == null || wordFiles.length == 0) {
                throw new RuntimeException("No files found!"); // Gérer les erreurs si les fichiers sont absents

            }

            // Traitez les fichiers reçus
            List<Resource> resources = Arrays.stream(wordFiles)
                    .map(file -> {
                        try {
                            return new ByteArrayResource(file.getBytes());

                        } catch (IOException e) {
                            throw new RuntimeException("Error processing file", e); // Gérer les erreurs
                        }
                    })
                    .collect(Collectors.toList());
            ragServiceImp.textEmbeddingWord(resources.toArray(new Resource[0]),utilisateurDto);

        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).build();
        }



    @PostMapping("/fichier/.xlsx")
    public ResponseEntity<Void> textEmbeddingsExcel(@RequestParam("files") MultipartFile[] excelFiles,
                                                    @RequestPart("user") UtilisateurDto utilisateurDto) {
        List<Resource> resources = Arrays.stream(excelFiles)
                .map(file -> {
                    try {
                        return new ByteArrayResource(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        ragServiceImp.textEmbeddingExcel(resources.toArray(new Resource[0]),utilisateurDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fichier/txt")
    public ResponseEntity<Void> textEmbeddingsTxt(@RequestParam("files") MultipartFile[] txtFiles) {
        List<Resource> resources = Arrays.stream(txtFiles)
                .map(file -> {
                    try {
                        return new ByteArrayResource(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // Appeler la fonction pour traiter les fichiers TXT
        ragServiceImp.textEmbeddingTxt(resources.toArray(new Resource[0]));

        // Retourner une réponse HTTP 200 (OK)
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fichier/.pptx")
    public ResponseEntity<Void> textEmbeddingsPowerpoint(@RequestParam("files") MultipartFile[] powerpointFiles,
                                                         @RequestPart("user") UtilisateurDto utilisateurDto) {
        List<Resource> resources = Arrays.stream(powerpointFiles)
                .map(file -> {
                    try {
                        return new ByteArrayResource(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        ragServiceImp.textEmbeddingPowerpoint(resources.toArray(new Resource[0]),utilisateurDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/image")
    public String Image(@RequestParam("prompt") String prompt){
        return ragServiceImp.Image(prompt);
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
