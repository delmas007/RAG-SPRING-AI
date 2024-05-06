package angaman.cedrick.rag_openai.Repository;

import angaman.cedrick.rag_openai.Model.vector_store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VectorRepository extends JpaRepository<vector_store, String> {

    @Query("delete from vector_store")
    void supprimerTout();
}
