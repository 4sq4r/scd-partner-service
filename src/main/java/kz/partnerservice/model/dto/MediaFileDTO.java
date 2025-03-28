package kz.partnerservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
@EqualsAndHashCode(callSuper = true)
public class MediaFileDTO extends BaseDTO {

    @JsonProperty(access = READ_ONLY)
    private String url;
}
