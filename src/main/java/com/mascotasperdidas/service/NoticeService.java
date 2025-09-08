package com.mascotasperdidas.service;

import com.mascotasperdidas.controller.model.NoticeRequestBody;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.model.enums.ReportStatus;
import com.mascotasperdidas.model.filters.Filter;
import com.mascotasperdidas.model.filters.FilterBuilder;
import com.mascotasperdidas.repositories.NoticeImageRepository;
import com.mascotasperdidas.repositories.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final ConversionService conversionService;

    public NoticeService(NoticeRepository noticeRepository, NoticeImageRepository noticeImageRepository, ConversionService conversionService) {
        this.noticeRepository = noticeRepository;
        this.noticeImageRepository = noticeImageRepository;
        this.conversionService = conversionService;
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
    public void update(UUID id, NoticeRequestBody noticeRequestBody) {
        checkTokenIfIsCorrect();
        Notice existingNotice = getNotice(id);
        existingNotice.update(noticeRequestBody);
        noticeRepository.save(existingNotice);
        if (!noticeRequestBody.getImages().isEmpty()) {
            noticeRequestBody.getImages().forEach(this::saveImages);
        }
    }

    @Transactional
    public UUID create(NoticeRequestBody noticeRequestBody) {
        Notice notice = createNotice(noticeRequestBody);
        if (!noticeRequestBody.getImages().isEmpty()) {
            noticeRequestBody.getImages().forEach(this::saveImages);
        }
        generateToken();
        sendNotification();
        return notice.getId();
    }

    private Notice createNotice(NoticeRequestBody noticeRequestBody) {
        return noticeRepository.save(noticeRequestBody.toDomain());
    }

    @Transactional
    public void resolve(UUID id) {
        checkTokenIfIsCorrect();
        Notice notice = getNotice(id);
        notice.setStatus(ReportStatus.resuelto);
        noticeRepository.save(notice);
    }

    private void saveImages(Object image) {
        //TODO
    }

    private void generateToken() {
        //TODO
    }

    private void sendNotification() {
        //TODO
    }

    private void checkTokenIfIsCorrect() {
        //TODO
    }
}
