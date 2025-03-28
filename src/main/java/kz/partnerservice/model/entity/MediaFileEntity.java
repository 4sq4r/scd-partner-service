package kz.partnerservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "media_files")
@EqualsAndHashCode(callSuper = true)
public class MediaFileEntity extends BaseEntity {

    @Column(name = "url", nullable = false)
    private String url;
}
