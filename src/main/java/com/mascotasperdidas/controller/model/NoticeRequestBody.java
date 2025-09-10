package com.mascotasperdidas.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.model.enums.NoticeType;
import com.mascotasperdidas.model.enums.Sizes;
import com.mascotasperdidas.model.enums.Species;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
public class NoticeRequestBody {
    @NotBlank(message = "title es obligatorio")
    private String title;
    @NotBlank(message = "description es obligatorio")
    private String description;
    @NotBlank(message = "specie es obligatorio")
    private String specie;
    private String location;
    @NotBlank(message = "noticeType es obligatorio")
    private String noticeType;
    private String name;
    private String race;
    private String color;
    private Integer age;
    private String zone;
    private String size;
    private Map<String, String> contactInfo;

    public Notice toDomain() {
        return Notice.builder()
                .title(title)
                .description(description)
                .specie(Species.valueOf(specie))
                .location(location)
                .noticeType(NoticeType.valueOf(noticeType))
                .name(name)
                .race(race)
                .color(color)
                .age(age)
                .size(size != null ? Sizes.valueOf(size) : null)
                .contactInfo(contactInfo)
                .build();
    }
}
