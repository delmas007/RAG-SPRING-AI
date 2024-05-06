package angaman.cedrick.rag_openai.Service.Imp;

import angaman.cedrick.rag_openai.Service.RagService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

//import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

@Service
public class RagServiceImp implements RagService {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public RagServiceImp(VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    VectorStore vectorStore;
    JdbcTemplate jdbcTemplate;


    @Override
    public String askLlm(String query) {
        List<Document> documentList = vectorStore.similaritySearch(query);

//        String systemMessageTemplate = """
//                Répondez à la question,en vous basant uniquement sur le CONTEXTE fourni.
//                CONTEXTE:
//                     {CONTEXTE}
//                """;
        String systemMessageTemplate = """
                Vous devez répondre à la question suivante en vous basant uniquement sur le CONTEXTE fourni ci-dessous. Ne fournissez aucune information qui n'est pas contenue dans ce contexte.
                
                Votre tâche est de :
                - Utiliser uniquement le CONTEXTE pour élaborer votre réponse.
                - Ne pas faire de suppositions ou ajouter des informations qui ne sont pas dans le CONTEXTE.
                - Si vous ne trouvez pas la réponse dans le CONTEXTE, indiquez que vous ne disposez pas des informations nécessaires.

                CONTEXTE:
                    {CONTEXTE}

                Notez que votre réponse doit être précise, concise, et axée sur la question.\s
                """;
        Message systemMessage = new SystemPromptTemplate(systemMessageTemplate)
                .createMessage(Map.of("CONTEXTE",documentList));
        UserMessage userMessage = new UserMessage(query);
        Prompt prompt = new Prompt(List.of(systemMessage,userMessage));
        OpenAiApi aiApi = new OpenAiApi(apiKey);
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .withModel("gpt-4-turbo-preview")
                .withTemperature(0F)
                .withMaxTokens(1500 )
                .build();
        OpenAiChatClient openAiChatClient = new OpenAiChatClient(aiApi, openAiChatOptions);
        ChatResponse response = openAiChatClient.call(prompt);
        String responseContent = response.getResult().getOutput().getContent();
       return responseContent;

    }

//    @Override
//    public String askLlm(String query) {
//        List<Document> documentList = vectorStore.similaritySearch(query);
//
//        String systemMessageTemplate = """
//                Donne la requête MongoDB de la question
//                CONTEXTE:
//                     {CONTEXTE}
//                """;
//        Message systemMessage = new SystemPromptTemplate(systemMessageTemplate)
//                .createMessage(Map.of("CONTEXTE",documentList));
//        UserMessage userMessage = new UserMessage(query);
//        Prompt prompt = new Prompt(List.of(systemMessage,userMessage));
//        OpenAiApi aiApi = new OpenAiApi(apiKey);
//        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
//                .withModel("gpt-4-turbo-preview")
//                .withTemperature(0F)
//                .withMaxTokens(800)
//                .build();
//        OpenAiChatClient openAiChatClient = new OpenAiChatClient(aiApi, openAiChatOptions);
//        ChatResponse response = openAiChatClient.call(prompt);
//        String responseContent = response.getResult().getOutput().getContent();
//        return responseContent;
//
//    }

    @Override
    public void textEmbeddingPdf(Resource[] pdfResources) {
        jdbcTemplate.update("delete from vector_store");
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.defaultConfig();
        String content = "";
        for(Resource resource : pdfResources){
            PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource,config);
            List<Document> documentList = pagePdfDocumentReader.get();
            content += documentList.stream().map(d -> d.getContent()).collect(Collectors.joining("\n"))+"\n";
        }

        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<String> chunks = tokenTextSplitter.split(content,1000);
        List<Document> chunksDocs = chunks.stream().map(chunk -> new Document(chunk)).collect(Collectors.toList());
        vectorStore.accept(chunksDocs);

    }

    @Override
    public void textEmbeddingWord(Resource[] worldResources) {
        jdbcTemplate.update("delete from vector_store");
        String content = "";
        for(Resource resource : worldResources){
            try (InputStream inputStream = resource.getInputStream()) {
                XWPFDocument document = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                content += extractor.getText();
            } catch (IOException e) {
                // Gérer l'erreur d'une manière appropriée
                e.printStackTrace();
            }
        }

        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<String> chunks = tokenTextSplitter.split(content,1000);
        List<Document> chunksDocs = chunks.stream().map(chunk -> new Document(chunk)).collect(Collectors.toList());
        vectorStore.accept(chunksDocs);
    }



    public void textEmbeddingBSON(Resource[] worldResources) throws IOException {

        jdbcTemplate.update("delete from vector_store");
        StringBuilder contentBuilder = new StringBuilder();

//        for (Resource resource : worldResources) {
//            try (InputStream inputStream = resource.getInputStream()) {
//                // Lire le fichier BSON en tant que Document
//                org.bson.Document  bsonDocument = org.bson.Document.parse(inputStream.toString());
//                // Convertir le Document BSON en JSON pour le traitement
//                String json = bsonDocument.toJson(JsonWriterSettings.builder().outputMode(JsonMode.SHELL).build());
//                contentBuilder.append(json);
//            } catch (IOException e) {
//                // Gérer l'erreur d'une manière appropriée
//                e.printStackTrace();
//            }
//        }
        for (Resource resource : worldResources) {
            InputStream inputStream = resource.getInputStream();
            // Utilisez un Scanner pour lire le contenu de l'InputStream
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String content = scanner.hasNext() ? scanner.next() : "";

            // Utilisez la classe Document.parse(String) pour parser le JSON
            org.bson.Document bsonDocument = org.bson.Document.parse(content);

            // Convertir le Document BSON en JSON pour le traitement
            String json = bsonDocument.toJson(JsonWriterSettings.builder().outputMode(JsonMode.SHELL).build());
            contentBuilder.append(json);

            String contente = contentBuilder.toString();

            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            List<String> chunks = tokenTextSplitter.split(contente, 1000);
            List<Document> chunksDocs = chunks.stream().map(chunk -> new Document(chunk)).collect(Collectors.toList());
            vectorStore.accept(chunksDocs);
        }
    }

    @Override
    public void textEmbeddingExcel(Resource[] excelResources) {
        jdbcTemplate.update("delete from vector_store");
        String content = "";
        for(Resource resource : excelResources){
            try (InputStream inputStream = resource.getInputStream()) {
                Workbook workbook = WorkbookFactory.create(inputStream);
                int numberOfSheets = workbook.getNumberOfSheets();
                for (int i = 0; i < numberOfSheets; i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    Iterator<Row> rowIterator = sheet.iterator();
                    while (((Iterator<?>) rowIterator).hasNext()) {
                        Row row = rowIterator.next();
                        Iterator<Cell> cellIterator = row.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            content += cell.toString() + " ";
                        }
                        content += "\n";
                    }
                }
            } catch (IOException e) {
                // Gérer l'erreur d'une manière appropriée
                e.printStackTrace();
            }
        }

        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<String> chunks = tokenTextSplitter.split(content, 1000);
        List<Document> chunksDocs = chunks.stream().map(Document::new).collect(Collectors.toList());
        vectorStore.accept(chunksDocs);
    }



    @Override
    public void textEmbeddingPowerpoint(Resource[] PowerpointResources) {
        jdbcTemplate.update("delete from vector_store");
        String content = "";
        for (Resource resource : PowerpointResources) {
            try (InputStream inputStream = resource.getInputStream()) {
                XMLSlideShow ppt = new XMLSlideShow(inputStream);
                for (XSLFSlide slide : ppt.getSlides()) {
                    for (XSLFShape shape : slide.getShapes()) {
                        if (shape instanceof XSLFTextShape) {
                            XSLFTextShape textShape = (XSLFTextShape) shape;
                            for (XSLFTextParagraph paragraph : textShape) {
                                content += paragraph.getText() + "\n";
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // Gérer l'erreur d'une manière appropriée
                e.printStackTrace();
            }
        }

        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<String> chunks = tokenTextSplitter.split(content, 1000);
        List<Document> chunksDocs = chunks.stream().map(Document::new).collect(Collectors.toList());
        vectorStore.accept(chunksDocs);
    }

    @Override
    public String Image(String prompt) {
        OpenAiImageApi api = new OpenAiImageApi(apiKey);
        OpenAiImageClient openaiImageClient = new OpenAiImageClient(api);

        ImageResponse response = openaiImageClient.call(
                new ImagePrompt(prompt,
                        OpenAiImageOptions.builder()
                                .withQuality("hd")
                                .withN(1)
                                .withHeight(1024)
                                .withWidth(1024).build())

        );
        return response.getResult().getOutput().getUrl();
    }

    public void textEmbeddingTxt(Resource[] txtResources) {
        jdbcTemplate.update("delete from vector_store"); // Effacer l'ancien contenu
        StringBuilder content = new StringBuilder();

        // Lire le contenu des fichiers TXT
        for (Resource resource : txtResources) {
            try (InputStream inputStream = resource.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                while ((line = reader.readLine()) != null) { // Lire ligne par ligne
                    content.append(line).append("\n"); // Ajouter au contenu
                }

            } catch (IOException e) {
                e.printStackTrace(); // Gérer l'erreur de lecture du fichier
            }
        }
    }

}

