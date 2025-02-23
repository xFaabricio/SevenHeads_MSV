package br.com.sevenheads.userService.domain.utility.api.v1;

import br.com.sevenheads.userService.config.JwtService;
import br.com.sevenheads.userService.domain.entity.User;
import br.com.sevenheads.userService.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BackblazeService {

	private final Environment environment;

	private final JwtService jwtService;

	private final UserRepository userRepository;
	private static final String B2_API_URL = "https://api.backblazeb2.com";

	public String authorizeAccount() throws IOException {
		OkHttpClient client = new OkHttpClient();

		String applicationKeyId = environment.getProperty("b2.application.key.id");
		String applicationKey = environment.getProperty("b2.application.key");

		String credentials = Credentials.basic(applicationKeyId, applicationKey);

		Request request = new Request.Builder()
				.url(B2_API_URL + "/b2api/v2/b2_authorize_account")
				.header("Authorization", credentials)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Failed to authorize account: " + response.message());
			}

			return Objects.requireNonNull(response.body()).string();
		}
	}

	public Response authorizeAccountResponse() throws IOException {
		OkHttpClient client = new OkHttpClient();

		// Autorize a conta
		String authorizationResponse = authorizeAccount();
		JSONObject json = new JSONObject(authorizationResponse);
		String authorizationToken = json.getString("authorizationToken");
		String apiUrl = json.getString("apiUrl");

		// Passo 1: Obtenha a URL de Upload
		String uploadUrlRequest = apiUrl + "/b2api/v2/b2_get_upload_url";
		JSONObject uploadRequestJson = new JSONObject();
		uploadRequestJson.put("bucketId", environment.getProperty("b2.bucket.id")); // Substitua pelo ID do seu bucket

		RequestBody requestBodyUploadUrl = RequestBody.create(uploadRequestJson.toString(), MediaType.parse("application/json"));
		Request uploadUrlRequestObj = new Request.Builder()
				.url(uploadUrlRequest)
				.post(requestBodyUploadUrl)
				.header("Authorization", authorizationToken)
				.build();

		Response uploadUrlResponse = client.newCall(uploadUrlRequestObj).execute();
		if (!uploadUrlResponse.isSuccessful()) {
			throw new IOException("Failed to get upload URL: " + uploadUrlResponse.message());
		}

		return uploadUrlResponse;
	}

	public String uploadPhoto(String fileName, byte[] fileContent, String authorizationHeader) throws IOException {
		return uploadPhoto(fileName, fileContent, authorizationHeader, null);
	}

	public String uploadPhoto(String fileName, byte[] fileContent, UUID idAPI) throws IOException {
		return uploadPhoto(fileName, fileContent, null, idAPI);
	}

	public String uploadPhoto(String fileName, byte[] fileContent, String authorizationHeader, UUID idAPI) throws IOException {
		OkHttpClient client = new OkHttpClient();

		Response uploadUrlResponse = authorizeAccountResponse();

		// Obtendo a URL de upload e o token específico para upload
		String uploadUrlResponseBody = Objects.requireNonNull(uploadUrlResponse.body()).string();
		JSONObject uploadUrlJson = new JSONObject(uploadUrlResponseBody);
		String uploadUrl = uploadUrlJson.getString("uploadUrl");
		String uploadAuthorizationToken = uploadUrlJson.getString("authorizationToken");

		Optional<User> user = Optional.empty();

		if(authorizationHeader != null) {
			final String jwtToken;
			final String login;

			if (!authorizationHeader.startsWith("Bearer ")) {
				return "UNAUTHORIZED";
			}

			jwtToken = authorizationHeader.substring(7);
			login = jwtService.extractLogin(jwtToken);

			 user = userRepository.findByLogin(login);
		} else if (idAPI != null) {
			user = userRepository.findByidApi(idAPI);
		}

		if(!user.isPresent()){
			throw new IOException("User not found");
		}

		// Passo 2: Faça o upload do arquivo
		RequestBody fileRequestBody = RequestBody.create(fileContent, MediaType.parse("application/octet-stream"));
		Request uploadRequest = new Request.Builder()
				.url(uploadUrl)
				.post(fileRequestBody)
				.header("Authorization", uploadAuthorizationToken)
				.addHeader("X-Bz-File-Name", "users_profile/"+ user.get().getIdApi() + "_" + fileName)
				.addHeader("Content-Type", "application/octet-stream")
				.addHeader("X-Bz-Content-Sha1", "do_not_verify")
				.build();

		try (Response response = client.newCall(uploadRequest).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Failed to upload photo: " + response.message());
			}

			// Verificar o corpo da resposta
			String responseBody = response.body().string();
			System.out.println("Response Body: " + responseBody); // Log para depuração
			JSONObject jsonResponse = new JSONObject(responseBody);

			if (jsonResponse.has("fileId")) {
				String fileId = jsonResponse.getString("fileId");
				user.get().setProfilePhotoId(fileId);
				userRepository.save(user.get());
			}

			return responseBody; // Retorna o corpo da resposta original
		}
	}

	public String getTemporaryDownloadLink(String fileName, long validityInSeconds) throws IOException {
		OkHttpClient client = new OkHttpClient();

		String authorizationResponse = authorizeAccount();
		JSONObject json = new JSONObject(authorizationResponse);

		String apiUrl = json.getString("apiUrl");
		String authToken = json.getString("authorizationToken");

		JSONObject requestBody = new JSONObject();
		requestBody.put("bucketId", environment.getProperty("b2.bucket.id"));
		requestBody.put("fileNamePrefix", fileName);
		requestBody.put("validDurationInSeconds", validityInSeconds);

		Request request = new Request.Builder()
				.url(apiUrl + "/b2api/v2/b2_get_download_authorization")
				.post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
				.header("Authorization", authToken)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Failed to get download authorization: " + response.message());
			}

			// Pega o token de autorização
			String responseBody = Objects.requireNonNull(response.body()).string();
			JSONObject downloadAuthResponse = new JSONObject(responseBody);
			String downloadAuthToken = downloadAuthResponse.getString("authorizationToken");

			// Monta a URL temporária
			String downloadUrl = json.getString("downloadUrl");
			return downloadUrl + "/file/" + environment.getProperty("b2.bucket.id") + "/" + fileName + "?Authorization=" + downloadAuthToken;
		}
	}
}