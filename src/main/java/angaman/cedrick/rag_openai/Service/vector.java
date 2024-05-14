package angaman.cedrick.rag_openai.Service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Optional;

public abstract class vector implements VectorStore {
    public vector(String id) {
        this.id = id;
    }

    private String id; // Ajouter une variable pour stocker l'ID



    // Implémenter la méthode pour récupérer l'ID
    public String getId() {
        return id;
    }

}
