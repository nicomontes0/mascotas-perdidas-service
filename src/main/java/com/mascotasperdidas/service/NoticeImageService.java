package com.mascotasperdidas.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.model.NoticeImage;
import com.mascotasperdidas.repositories.NoticeImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class NoticeImageService {

    @Value("${image-converter.url}")
    private String imageConverterUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Cloudinary cloudinary;
    private final NoticeImageRepository noticeImageRepository;

    public NoticeImageService(Cloudinary cloudinary, NoticeImageRepository noticeImageRepository) {
        this.cloudinary = cloudinary;
        this.noticeImageRepository = noticeImageRepository;
    }

    public void create(Notice notice, MultipartFile file) {
        try {
            String url = uploadImageToCloudinary(file);
            float[] vectorImage = getVectorImage(file);
            NoticeImage noticeImage = new NoticeImage();
            noticeImage.setNotice(notice);
            noticeImage.setImageUrl(url);
            noticeImage.setVector(vectorImage);
            noticeImageRepository.save(noticeImage);
            log.info("Image created successfully: {}", noticeImage);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private float[] getVectorImage(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentDisposition(ContentDisposition.builder("form-data")
                .name("file")
                .filename(file.getOriginalFilename())
                .build());
        partHeaders.setContentType(MediaType.parseMediaType(file.getContentType() != null ? file.getContentType() : "application/octet-stream"));

        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(fileAsResource, partHeaders);
        body.add("file", fileEntity);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                imageConverterUrl.concat("/calculate_vector"),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error en request: " + response.getStatusCode() + " - " + response.getBody());
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode vectorNode = root.path("vector");

        float[] vector = new float[vectorNode.size()];
        for (int i = 0; i < vectorNode.size(); i++) {
            vector[i] = vectorNode.get(i).floatValue();
        }

        return vector;
    }

    private String uploadImageToCloudinary(MultipartFile file) throws IOException {
        Map<String, String> params = ObjectUtils.asMap(
                "folder", "mascotasPerdidasApp/images"
        );
        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), params);
        return result.get("secure_url").toString();
    }

}
