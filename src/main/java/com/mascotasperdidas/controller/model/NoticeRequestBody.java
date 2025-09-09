package com.mascotasperdidas.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.model.enums.NoticeType;
import com.mascotasperdidas.model.enums.Sizes;
import com.mascotasperdidas.model.enums.Species;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoticeRequestBody {
    private String title;
    private String description;
    private String specie;
    private String location;
    private String noticeType;
    private String name;
    private String race;
    private String color;
    private int age;
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
                .zone(zone)
                .size(Sizes.valueOf(size))
                .contactInfo(contactInfo)
                .build();
    }
}
