package com.mascotasperdidas.controller;

import com.mascotasperdidas.controller.model.NoticeRequestBody;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam Map<String,String> queryParams
    ) {
        log.info("Se llama a /api/notices con queryParams: {}", queryParams.toString());
        queryParams.keySet().removeAll(nonFilterKeys);

        Page<Notice> reportPage = noticeService.get(page, size, queryParams);
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

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNotice(
            @PathVariable("id") UUID id,
            @RequestBody NoticeRequestBody notice) {
        log.info("Se llama al servicio de actualización de un notice con id {} y body {}", id, notice);
        noticeService.update(id, notice);
        log.info("Se han modificado los datos del notice: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<Void> createNotice(@RequestBody NoticeRequestBody notice) {
        log.info("Se llama al servicio de creacion de un notice con body {}", notice);
        UUID id = noticeService.create(notice);
        log.info("Se ha creado el notice con id: {}", id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Void> resolveNotice(@PathVariable UUID id) {
        log.info("Se llama a /api/notices/{}/resolve", id);
        noticeService.resolve(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
