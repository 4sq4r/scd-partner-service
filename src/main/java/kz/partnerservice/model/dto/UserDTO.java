package kz.partnerservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO {

    private String username;
    private String firstName;
    private String lastName;
    @Positive
    private Long phoneNumber;
    private String address;
    @JsonProperty(access = READ_ONLY)
    private MediaFileDTO mediaFile;
}
