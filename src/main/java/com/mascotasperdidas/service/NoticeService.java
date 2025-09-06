package com.mascotasperdidas.service;

import com.mascotasperdidas.controller.model.NoticeRequestBody;
import com.mascotasperdidas.model.enums.ReportStatus;
import com.mascotasperdidas.model.filters.Filter;
import com.mascotasperdidas.model.filters.FilterBuilder;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.repositories.NoticeImageRepository;
import com.mascotasperdidas.repositories.NoticeRepository;
import com.mascotasperdidas.utils.FiltersUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;

    public NoticeService(NoticeRepository noticeRepository, NoticeImageRepository noticeImageRepository) {
        this.noticeRepository = noticeRepository;
        this.noticeImageRepository = noticeImageRepository;
    }

    public Page<Notice> get(int page, int size, Map<String,String> params) {
        List<Filter> filters = new FilterBuilder().build(FiltersUtils.removeNotFilters(params));
        List<Filter> notFilters = new FilterBuilder().build(FiltersUtils.removeFilters(params));

        //TODO IMPLEMENT FILTERS AGAINST REPOSITORY
        //Page<Report> reportPage = reportRepository.findAll(page, size, filters, notFilters);

        //log.info("Se obtuvo la pagina de {} reports", reportPage.getContent().size());

        return null;
    }

    public Notice get(UUID id) {
        return getNotice(id);
    }

    private Notice getNotice(UUID id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el notice"));
    }

    public void update(UUID id, NoticeRequestBody noticeRequestBody) {
        checkTokenIfIsCorrect();
        getNotice(id);
        Notice notice = createNotice(noticeRequestBody);
        noticeRepository.save(notice);
        noticeRequestBody.getImages().forEach(this::saveImages);
    }

    public UUID create(NoticeRequestBody noticeRequestBody) {
        Notice notice = createNotice(noticeRequestBody);
        noticeRequestBody.getImages().forEach(this::saveImages);
        generateToken();
        sendNotification();
        return notice.getId();
    }

    private Notice createNotice(NoticeRequestBody noticeRequestBody) {
        return noticeRepository.save(noticeRequestBody.toDomain());
    }

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
