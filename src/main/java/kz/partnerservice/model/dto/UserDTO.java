package kz.partnerservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO {

    @NotNull
    private String username;

    @NotNull
    @JsonProperty(access = WRITE_ONLY)
    private String password;
}
