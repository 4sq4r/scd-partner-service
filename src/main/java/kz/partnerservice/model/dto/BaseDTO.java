package kz.partnerservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
public class BaseDTO {

    @JsonProperty(access = READ_ONLY)
    private Long id;

    @JsonProperty(access = READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = READ_ONLY)
    private LocalDateTime updatedAt;
}
