package com.mascotasperdidas.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.model.NoticeImage;
import com.mascotasperdidas.repositories.NoticeImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class NoticeImageService {

    private final Cloudinary cloudinary;
    private final NoticeImageRepository noticeImageRepository;

    public NoticeImageService(Cloudinary cloudinary, NoticeImageRepository noticeImageRepository) {
        this.cloudinary = cloudinary;
        this.noticeImageRepository = noticeImageRepository;
    }

    public void create(Notice notice, MultipartFile file) {
        try {
            String url = uploadImageToCloudinary(file);
            NoticeImage noticeImage = new NoticeImage();
            noticeImage.setNotice(notice);
            noticeImage.setImageUrl(url);
            noticeImageRepository.save(noticeImage);
            log.info("Image created successfully: {}", noticeImage);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String uploadImageToCloudinary(MultipartFile file) throws IOException {
        Map<String, String> params = ObjectUtils.asMap(
                "folder", "mascotasPerdidasApp/images"
        );
        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), params);
        return result.get("secure_url").toString();
    }

}
