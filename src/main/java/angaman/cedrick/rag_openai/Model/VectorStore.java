package angaman.cedrick.rag_openai.Model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "vector_store")
public class VectorStore {

  @Id
  private String id;

  @Column(name = "content")
  private String content;

  @Column(name = "metadata", columnDefinition = "jsonb")
  private String metadata;

  @Column(name = "embedding", columnDefinition = "vector(3)")  // 3 est la dimension du vecteur
  private float[] embedding;  // Utiliser float[] pour représenter le vecteur en Java

}


