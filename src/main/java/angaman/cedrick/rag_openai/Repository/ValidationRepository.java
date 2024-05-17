package angaman.cedrick.rag_openai.Repository;

import angaman.cedrick.rag_openai.Model.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Integer> {
}
