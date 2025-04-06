package br.com.sevenheads.userService.domain.utility.api.v1;

import br.com.sevenheads.userService.utility.cohere.CohereIARequest;
import br.com.sevenheads.userService.utility.cohere.CohereIAResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CohereService {

    private final Environment environment;

    public CohereIAResponse callPromptCohere (CohereIARequest request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String apiKey = environment.getProperty("cohere.api.key");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("Chave da API Cohere não configurada");
        }

        String jsonBody = objectMapper.writeValueAsString(request);

        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        Request httpRequest = new Request.Builder()
                .url("https://api.cohere.ai/v1/generate")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Erro na API Cohere: " + response.code() + " - " + response.message() != null ? response.message() : "Mensagem de erro desconhecida");
            }

            if(response.body() != null) {
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, CohereIAResponse.class);
            }else{
                return null;
            }
        }
    }

    public String callPromptSimplyfiedCohere(String message, String model) throws IOException {
        if(model == null || model.isEmpty()){
            model = "command";
        }
        CohereIARequest request = new CohereIARequest(model, message, 0.7, 100);
        CohereIAResponse response = callPromptCohere(request);
        return response.getGenerations().get(0).getText();
    }

}
