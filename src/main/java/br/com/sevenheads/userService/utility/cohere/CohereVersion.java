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
public class CohereVersion {

    @JsonProperty("version")
    private String version;

}
