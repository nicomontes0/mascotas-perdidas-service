package com.mascotasperdidas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mascotasperdidas.controller.model.NoticeRequestBody;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.service.NoticeService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/notices")
@Slf4j
public class NoticeController {

    private static final Set<String> nonFilterKeys = Set.of("page", "size");
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
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
    public ResponseEntity<Void> updateNotice(
            @PathVariable("id") UUID id,
            @RequestPart("notice") String noticeJson,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages
    ) {
        log.info("Se llama al servicio de actualización de un notice con id {} y body {}", id, noticeJson);
        try {
            noticeService.update(id, createNoticeRequestBody(noticeJson), newImages);
            log.info("Se han modificado los datos del notice: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> createNotice(
            @RequestPart("notice") String noticeJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        log.info("Se llama al servicio de creacion de un notice con body {}", noticeJson);
        try {
            UUID id = noticeService.create(createNoticeRequestBody(noticeJson), images);
            log.info("Se ha creado el notice con id: {}", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveNotice(@PathVariable UUID id) {
        log.info("Se llama a /api/notices/{}/resolve", id);
        noticeService.resolve(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private NoticeRequestBody createNoticeRequestBody(String noticeJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(noticeJson, NoticeRequestBody.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
