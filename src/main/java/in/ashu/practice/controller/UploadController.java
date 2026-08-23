package in.ashu.practice.controller;

import in.ashu.practice.dto.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
public class UploadController {

    private final VectorStore vectorStore;

    @PostMapping("/upload")
    public UploadResponse upload(@RequestParam("file") MultipartFile file) throws IOException {
        log.debug("Uploading file {}", file.getOriginalFilename());

        Path destinationFile = Paths.get("/temp").resolve(Paths.get(file.getOriginalFilename())).normalize().toAbsolutePath();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        TikaDocumentReader documentReader = new TikaDocumentReader(destinationFile.toUri().toString());
        List<Document> documents = documentReader.get();
        List<Document> splitDocuments = TokenTextSplitter.builder().build().apply(documents);

        vectorStore.add(splitDocuments);
        log.debug("Uploaded {} files", splitDocuments.size());
        return new UploadResponse(file.getOriginalFilename(), file.getContentType(), file.getSize());
    }
}
