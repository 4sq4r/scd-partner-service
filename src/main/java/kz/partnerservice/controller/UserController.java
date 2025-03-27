package kz.partnerservice.controller;

import jakarta.validation.Valid;
import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.UserDTO;
import kz.partnerservice.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PutMapping
    public UserDTO updateOne(@RequestBody @Valid UserDTO userDTO) throws CustomException {
        return userService.updateOne(userDTO);
    }
}
