package angaman.cedrick.rag_openai.Model;


import jakarta.persistence.*;
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
public class vector_store {

    @Id
    private String id ;

    @Column(name = "content")
    private String content;

    @Column(name = "embedding")
    private Integer embedding;
}
