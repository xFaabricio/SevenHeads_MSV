package br.com.sevenheads.userService.domain.utility.api.v1;

import br.com.sevenheads.userService.domain.entity.User;
import br.com.sevenheads.userService.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/utility")
@RequiredArgsConstructor
public class UtilityController {

    private final BackblazeService backblazeService;

    private final UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("file") MultipartFile file) {
        try {
            String response = backblazeService.uploadPhoto(file.getOriginalFilename(), file.getBytes(), authorizationHeader);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/upload/idAPI/{idAPI}")
    public ResponseEntity<String> uploadPhoto(@PathVariable UUID idAPI, @RequestParam("file") MultipartFile file) {
        Optional<User> user = userRepository.findByidApi(idAPI);
        if(user.isPresent()) {
            try {
                String response = backblazeService.uploadPhoto(file.getOriginalFilename(), file.getBytes(), idAPI);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/image/IdAPI/{idAPI}/{fileId}")
    public ResponseEntity<byte[]> getImageIdAPI(@PathVariable UUID idAPI, @PathVariable String fileId) {
        Optional<User> user = userRepository.findByidApi(idAPI);
        if(user.isPresent()) {
            return getImage(fileId);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/image/{fileId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileId) {
        try {
            // Obter o token de autorização e a URL da imagem
            String authorizationResponse = backblazeService.authorizeAccount();
            JSONObject json = new JSONObject(authorizationResponse);
            String authorizationToken = json.getString("authorizationToken");
            String apiUrl = json.getString("apiUrl");

            // Solicitar a imagem do Backblaze
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl + "/b2api/v2/b2_download_file_by_id?fileId=" + fileId)
                    .header("Authorization", authorizationToken)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }

                byte[] imageBytes = Objects.requireNonNull(response.body()).bytes();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG); // Altere o tipo de mídia conforme necessário
                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/temporary-link")
    public ResponseEntity<String> getTemporaryLink(@RequestParam("fileName") String fileName,
                                                   @RequestParam("validity") long validityInSeconds) {
        try {
            String temporaryLink = backblazeService.getTemporaryDownloadLink(fileName, validityInSeconds);
            return ResponseEntity.ok(temporaryLink);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
