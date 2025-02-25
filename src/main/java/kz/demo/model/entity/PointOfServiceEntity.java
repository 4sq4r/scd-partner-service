package kz.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "points_of_service")
@EqualsAndHashCode(callSuper = true)
public class PointOfServiceEntity extends BaseEntity {

    @Column(nullable = false)
    private String address;
    @Column(nullable = false, unique = true, length = 13)
    private String phone;

    @JoinColumn(name = "company_id", updatable = false, nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyEntity company;

}
