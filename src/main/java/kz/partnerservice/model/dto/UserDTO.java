package kz.partnerservice.model.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO {

    private String username;
    private String firstName;
    private String lastName;

    @Positive
    private Long phoneNumber;

    private String address;
}
