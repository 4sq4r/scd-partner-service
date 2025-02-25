package kz.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "companies")
@EqualsAndHashCode(callSuper = true)
public class CompanyEntity extends BaseEntity {

    @Column(unique = true, length = 13, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, length = 100, nullable = false)
    private String name;
}
