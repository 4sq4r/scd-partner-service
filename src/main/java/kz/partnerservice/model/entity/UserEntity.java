package kz.partnerservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static jakarta.persistence.CascadeType.REMOVE;

@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends BaseEntity {

    @Column(unique = true, nullable = false, length = 32)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 11, unique = true)
    private Long phoneNumber;

    @Column(name = "address")
    private String address;

    @OneToOne(mappedBy = "user", optional = false, cascade = REMOVE)
    private UserMediaFileEntity mediaFile;
}
