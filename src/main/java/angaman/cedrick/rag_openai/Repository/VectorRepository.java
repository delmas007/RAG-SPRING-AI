package angaman.cedrick.rag_openai.Repository;

import angaman.cedrick.rag_openai.Model.VectorStore;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VectorRepository extends JpaRepository<VectorStore, String> {

    @Modifying  // Indique que la requête modifie des données
    @Transactional
    @Query("delete from VectorStore v where v.utilisateur.id = :utilisateurId")
    void supprimerParUtilisateurId(@Param("utilisateurId") String utilisateurId);


    @Transactional(readOnly = true)
    @Query("SELECT v FROM VectorStore v")
    List<VectorStore> findAllVectors();

    @Transactional
    @Query("SELECT v FROM VectorStore v WHERE v.id = :id")
    Optional<VectorStore> findByIdd(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE VectorStore v SET v.utilisateur.id = :userId WHERE v.id = :vectorId")
    void updateUserForVector(@Param("userId") String userId, @Param("vectorId") String vectorId);

}
