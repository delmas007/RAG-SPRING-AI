package angaman.cedrick.rag_openai.Controller;

import angaman.cedrick.rag_openai.Dto.UtilisateurDto;
import angaman.cedrick.rag_openai.Service.Imp.RagServiceImp;
import angaman.cedrick.rag_openai.Service.Imp.ResendMailImp;
import angaman.cedrick.rag_openai.Service.Imp.UtilisateurServiceImp;
import angaman.cedrick.rag_openai.Service.Imp.ValidationServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
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
    ResendMailImp resendMailImp;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/inscription/")
    public UtilisateurDto save(@RequestBody UtilisateurDto dto) {
        String role = "USER";
        return utilisateurServiceImp.Inscription(dto,role);
    }

    @PostMapping("/connexion")
    public ResponseEntity<Map<String, String>> Connexion(@RequestBody Map<String, String> authentification) {
        String username = authentification.get("username");
        String password = authentification.get("password");

        return utilisateurServiceImp.Connexion(username, password);
    }

    @PostMapping("/resendMail/")
    public int resend(@RequestBody String email) {
        return resendMailImp.resendMail(email);
    }

    @PostMapping("/activation/")
    public int activation(@RequestBody Map<String, String> codes) {
        String code = codes.get("code");
        return utilisateurServiceImp.activation(code);
    }

    @PostMapping("/modifierMotDePasse/")
    public int motDePasse(@RequestBody Map<String, String> username) {
        String emaile = username.get("email");
        return utilisateurServiceImp.motDePasse(emaile);
    }

    @PostMapping("/NouveauMotDePasse/")
    public int NouveauMotDePasse(@RequestBody Map<String, String> donnees) {
        return utilisateurServiceImp.NouveauMotDePasse(donnees);
    }


    @PostMapping("/rag/")
    public ResponseEntity<Map<String, Object>> rag(@RequestParam(name = "query") String query,
                                                   @RequestBody UtilisateurDto utilisateurDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("result", ragServiceImp.askLlm(query, utilisateurDto));
            return ResponseEntity.ok(response);
        } catch (NonTransientAiException e) {
            if (e.getMessage().contains("rate_limit_exceeded")) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of("error", "Limite de requêtes dépassée. Veuillez réessayer plus tard."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Erreur interne du serveur. Veuillez réessayer plus tard."));
            }
        } catch (TransientAiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur temporaire du serveur. Veuillez réessayer plus tard."));
        }
    }
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

}
