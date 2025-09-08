package com.mascotasperdidas.service;

import com.mascotasperdidas.controller.model.NoticeRequestBody;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.model.NoticeDTO;
import com.mascotasperdidas.model.enums.ReportStatus;
import com.mascotasperdidas.model.filters.Filter;
import com.mascotasperdidas.model.filters.FilterBuilder;
import com.mascotasperdidas.repositories.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ConversionService conversionService;
    private final NoticeImageService noticeImageService;
    private final TokenService tokenService;
    private final EmailService emailService;

    public NoticeService(NoticeRepository noticeRepository, ConversionService conversionService, NoticeImageService noticeImageService, TokenService tokenService, EmailService emailService) {
        this.noticeRepository = noticeRepository;
        this.conversionService = conversionService;
        this.noticeImageService = noticeImageService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    public Page<Notice> get(Map<String, String> params, Pageable pageable) {
        List<Filter> filters = new FilterBuilder().build(params);
        FiltersSpecification<Notice> spec = new FiltersSpecification<>(filters, conversionService);
        Page<Notice> noticePage = noticeRepository.findAll(spec, pageable);
        log.info("Se obtuvo la pagina de {} notices", noticePage.getContent().size());
        return noticePage;
    }

    public Notice get(UUID id) {
        return getNotice(id);
    }

    private Notice getNotice(UUID id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el notice"));
    }

    @Transactional
    public void update(UUID id, NoticeRequestBody noticeRequestBody, List<MultipartFile> newImages) {
        Notice existingNotice = getNotice(id);
        existingNotice.update(noticeRequestBody);
        noticeRepository.save(existingNotice);
        if (newImages != null && !newImages.isEmpty()) {
            //TODO QUE HACEMOS CON IMAGENES ANTERIORES
            newImages.forEach(image -> noticeImageService.create(existingNotice, image));
        }
    }

    @Transactional
    public NoticeDTO create(NoticeRequestBody noticeRequestBody, List<MultipartFile> images) {
        Notice notice = createNotice(noticeRequestBody);
        if (images != null && !images.isEmpty()) {
            images.forEach(image -> noticeImageService.create(notice, image));
        }
        String jwt = tokenService.generateTokenForNotice(notice.getId());
        NoticeDTO noticeDTO = NoticeDTO.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .contactInfo(notice.getContactInfo())
                .token(jwt)
                .build();
        emailService.sendEmailForNotice(noticeDTO);
        return noticeDTO;
    }

    private Notice createNotice(NoticeRequestBody noticeRequestBody) {
        return noticeRepository.save(noticeRequestBody.toDomain());
    }

    @Transactional
    public void resolve(UUID id) {
        Notice notice = getNotice(id);
        notice.setStatus(ReportStatus.resuelto);
        noticeRepository.save(notice);
    }

}
