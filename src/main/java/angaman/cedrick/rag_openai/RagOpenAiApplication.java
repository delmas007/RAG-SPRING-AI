package angaman.cedrick.rag_openai;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
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
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@PropertySource("classpath:Key/.env")
public class RagOpenAiApplication {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public static void main(String[] args) {
        SpringApplication.run(RagOpenAiApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner (VectorStore vectorStore, JdbcTemplate jdbcTemplate,
                                         @Value("classpath:pdfs/*") Resource[] resources) {
        return args -> {
//            textEmbedding(vectorStore, jdbcTemplate, resources);
            String query = "dans les memoires donne moi toute les thematic utilisées";
            askLlm(vectorStore, query);


        };
    }

    private void askLlm(VectorStore vectorStore,String query) {
        //            String query = "donne moi pour chaque memoire au format json, l'auteur du memoire, " +
        //                    "les membres du jury, un petit résumé du mémoire et les languages utilisées.";
        //        String query = "dans les memoires donne moi toute les thematic utilisées";
        List<Document> documentList = vectorStore.similaritySearch(query);
        System.out.println(documentList);
        String systemMessageTemplate = """
                Répondez à la question, au format json mais n'ajoute pas ```json   ``` ,en vous basant uniquement sur le CONTEXTE fourni.
                Si la réponse n'est pas trouvée dans le contexte, répondez ' je ne sais pas '.
                CONTEXTE:
                     {CONTEXTE}
                """;
        Message systemMessage = new SystemPromptTemplate(systemMessageTemplate)
                .createMessage(Map.of("CONTEXTE",documentList));
        UserMessage userMessage = new UserMessage(query);
        Prompt prompt = new Prompt(List.of(systemMessage,userMessage));
        OpenAiApi aiApi = new OpenAiApi(apiKey);
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .withModel("gpt-4-turbo-preview")
                .withTemperature(0F)
                .withMaxTokens(800)
                .build();
        OpenAiChatClient openAiChatClient = new OpenAiChatClient(aiApi, openAiChatOptions);
        ChatResponse response = openAiChatClient.call(prompt);
        String responseContent = response.getResult().getOutput().getContent();
        System.out.println(responseContent);
    }

    private static void textEmbedding(VectorStore vectorStore, JdbcTemplate jdbcTemplate, Resource[] pdfResources) {
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

}
