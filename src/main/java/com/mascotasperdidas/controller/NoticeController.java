package com.mascotasperdidas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mascotasperdidas.controller.model.NoticeRequestBody;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.model.NoticeDTO;
import com.mascotasperdidas.service.NoticeService;
import com.mascotasperdidas.service.TokenService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mascotasperdidas.utils.JwtUtils.extractJwtFromHeader;

@RestController
@RequestMapping("/api/notices")
@Slf4j
public class NoticeController {

    private static final Set<String> nonFilterKeys = Set.of("page", "size", "sort");
    private final NoticeService noticeService;
    private final TokenService tokenService;
    private final Validator validator;

    public NoticeController(NoticeService noticeService, TokenService tokenService, Validator validator) {
        this.noticeService = noticeService;
        this.tokenService = tokenService;
        this.validator = validator;
    }

    @GetMapping
    public Page<Notice> getNotices(
            @RequestParam Map<String, String> queryParams,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("Se llama a /api/notices con queryParams: {}", queryParams.toString());
        queryParams.keySet().removeAll(nonFilterKeys);

        Page<Notice> reportPage = noticeService.get(queryParams, pageable);
        log.info("Se obtiene la pagina de notices: {}", reportPage.getContent());
        return reportPage;
    }

    @GetMapping("/{id}")
    public Notice getNotice(@PathVariable UUID id) {
        log.info("Se llama a /api/notices con id: {}", id);
        Notice notice = noticeService.get(id);
        log.info("Se obtiene el notice: {}", notice);
        return notice;
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateNotice(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("id") UUID id,
            @RequestPart("notice") String noticeJson,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages
    ) {
        if (!isTokenValid(authorization, id)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        log.info("Se llama al servicio de actualización de un notice con id {} y body {}", id, noticeJson);
        try {
            noticeService.update(id, createNoticeRequestBody(noticeJson), newImages);
            log.info("Se han modificado los datos del notice: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNotice(
            @RequestPart("notice") String noticeJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        log.info("Se llama al servicio de creacion de un notice con body {}", noticeJson);
        try {
            NoticeDTO response = noticeService.create(createNoticeRequestBody(noticeJson), images);
            log.info("Se ha creado el notice {}", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveNotice(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id) {
        if (!isTokenValid(authorization, id)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        log.info("Se llama a /api/notices/{}/resolve", id);
        noticeService.resolve(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/{id}/manage")
    public ResponseEntity<?> manageAction(@PathVariable UUID id, @RequestHeader("Authorization") String authorization) {
        return isTokenValid(authorization, id) ?
                ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private boolean isTokenValid(String authorization, UUID id) {
        return tokenService.validateTokenAndOwnership(extractJwtFromHeader(authorization), id);
    }

    private NoticeRequestBody createNoticeRequestBody(String noticeJson) throws JsonProcessingException, IllegalArgumentException {
        ObjectMapper mapper = new ObjectMapper();
        NoticeRequestBody body;
        try {
            body = mapper.readValue(noticeJson, NoticeRequestBody.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw e;
        }

        Set<ConstraintViolation<NoticeRequestBody>> violations = validator.validate(body);
        if (!violations.isEmpty()) {
            String errorMsg = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Error de validación: " + errorMsg);
        }

        return body;
    }
}
