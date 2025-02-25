package kz.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PointOfServiceDTO extends BaseDTO {

    @NotNull
    private String address;
    @NotNull
    private String phone;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long companyId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CompanyDTO company;
}
