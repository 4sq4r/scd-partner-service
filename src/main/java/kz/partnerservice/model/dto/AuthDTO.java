package kz.partnerservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
public class AuthDTO {

    @NotNull
    @JsonProperty(access = WRITE_ONLY)
    private String username;

    @NotNull
    @JsonProperty(access = WRITE_ONLY)
    private String password;

    @JsonProperty(access = READ_ONLY)
    private String token;
}
