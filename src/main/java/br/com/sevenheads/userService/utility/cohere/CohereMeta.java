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
public class CohereMeta {

    @JsonProperty("api_version")
    private CohereVersion apiVersion;

    @JsonProperty("billed_units")
    private CohereBilledUnits billedUnits;

}
