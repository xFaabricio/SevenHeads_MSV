package br.com.sevenheads.userService.domain.utility.api.v1;

import br.com.sevenheads.userService.domain.entity.User;
import br.com.sevenheads.userService.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            // Verifica o tamanho do arquivo
            if (file.getSize() > 1048576) { // 1MB em bytes
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds 1MB limit.");
            }

            String response = backblazeService.uploadPhoto(file.getOriginalFilename(), file.getBytes(), authorizationHeader);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/upload/idAPI/{idAPI}")
    public ResponseEntity<String> uploadPhoto(@PathVariable UUID idAPI, @RequestParam("file") MultipartFile file) {
        // Verifica o tamanho do arquivo
        if (file.getSize() > 1048576) { // 1MB em bytes
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds 1MB limit.");
        }

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

                // Obter os bytes da imagem
                byte[] imageBytes = Objects.requireNonNull(response.body()).bytes();

                // Obter o nome do arquivo do header
                String fileName = response.header("X-Bz-File-Name", "image.jpg"); // Default para "image.jpg" se não houver header

                // Extrair a extensão do arquivo
                String fileExtension = getFileExtension(fileName);

                // Definir o tipo de mídia correto
                MediaType mediaType = getMediaType(fileExtension);

                // Criar os headers da resposta
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(mediaType);
                headers.setContentDisposition(ContentDisposition.builder("inline")
                        .filename(fileName) // Usa o nome real do arquivo
                        .build());

                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private MediaType getMediaType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case ".png": return MediaType.IMAGE_PNG;
            case ".jpg": case ".jpeg": return MediaType.IMAGE_JPEG;
            case ".gif": return MediaType.IMAGE_GIF;
            default: return MediaType.APPLICATION_OCTET_STREAM; // Para arquivos desconhecidos
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex); // Inclui o ponto na extensão, ex: ".png"
        }
        return ".jpg"; // Default caso não tenha extensão
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
