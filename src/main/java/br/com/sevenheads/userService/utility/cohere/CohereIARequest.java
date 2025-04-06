package br.com.sevenheads.userService.utility.cohere;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CohereIARequest {

    String model;

    String prompt;

    @JsonProperty("temperature")
    Double temperature;

    @JsonProperty("max_tokens")
    Integer maxTokens;
}