package angaman.cedrick.rag_openai.Repository;

import angaman.cedrick.rag_openai.Model.vector_store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface VectorRepository extends JpaRepository<vector_store, String> {

    @Modifying  // Indique que la requête modifie des données
    @Transactional
    @Query("delete from vector_store")
    void supprimerTout();
}
