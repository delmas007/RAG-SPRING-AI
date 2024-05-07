package angaman.cedrick.rag_openai.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum ErrorCodes {
    UTILISATEUR_PAS_TROUVER(900),
    UTILISATEUR_DEJA_EXIST(800);

    @Getter
    @Setter
    private int code;
}
