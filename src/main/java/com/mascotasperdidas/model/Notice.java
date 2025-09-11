package com.mascotasperdidas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mascotasperdidas.controller.model.NoticeRequestBody;
import com.mascotasperdidas.model.enums.ReportStatus;
import com.mascotasperdidas.model.enums.NoticeType;
import com.mascotasperdidas.model.enums.Sizes;
import com.mascotasperdidas.model.enums.Species;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "reports",
        indexes = {
                @Index(name = "idx_reports_type_status", columnList = "report_type, status"),
                @Index(name = "idx_reports_anon_user_token", columnList = "anon_user_token", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notice {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "created_at", columnDefinition = "timestamptz")
    private OffsetDateTime createdAt;

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name="species", nullable = false, columnDefinition = "species_enum")
    private Species specie;

    private String location;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "report_type", nullable = false, columnDefinition = "report_type_enum")
    private NoticeType noticeType;

    @Column(columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String race;

    @Column(columnDefinition = "text")
    private String color;

    @Column(columnDefinition = "INTEGER")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition="size_enum")
    private Sizes size;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "report_status_enum")
    private ReportStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_info", columnDefinition = "jsonb")
    private Map<String, String> contactInfo;

    @Column(name = "is_reported")
    private Boolean isReported;

    @Column(name = "anon_user_token", columnDefinition = "uuid", unique = true)
    private UUID anonUserToken;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeImage> images;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (status == null) status = ReportStatus.abierto;
        if (isReported == null) isReported = false;
    }

    public void update(NoticeRequestBody noticeRequestBody) {
        this.setTitle(noticeRequestBody.getTitle());
        this.setDescription(noticeRequestBody.getDescription());
        this.setSpecie(Species.valueOf(noticeRequestBody.getSpecie()));
        this.setLocation(noticeRequestBody.getLocation());
        this.setNoticeType(NoticeType.valueOf(noticeRequestBody.getNoticeType()));
        this.setContactInfo(noticeRequestBody.getContactInfo());
    }
}
