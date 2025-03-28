package kz.partnerservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Data
@Entity
@Table(name = "user_media_files")
@EqualsAndHashCode(callSuper = true)
public class UserMediaFileEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = LAZY, cascade = REMOVE)
    @JoinColumn(name = "media_file_id")
    private MediaFileEntity mediaFile;
}
