package br.com.sevenheads.userService.utility.cohere;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CohereIAResponse {

    private String id;
    private List<CohereGenerations> generations;
    private String prompt;

    @JsonProperty("meta")
    private CohereMeta meta;

    public CohereIAResponse(String errorMessage) {
        CohereIAResponse cohereIAResponse = new CohereIAResponse();
        CohereGenerations cohereGenerations = new CohereGenerations();
        cohereGenerations.setId(null);
        cohereGenerations.setText(errorMessage);
        cohereGenerations.setFinishReason(null);
        cohereIAResponse.setGenerations(Collections.singletonList(cohereGenerations));
        cohereIAResponse.setId(null);
        cohereIAResponse.setPrompt(null);
        cohereIAResponse.setMeta(null);
    }

}
