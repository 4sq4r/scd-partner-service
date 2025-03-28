package kz.partnerservice.controller;

import jakarta.validation.Valid;
import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.MediaFileDTO;
import kz.partnerservice.model.dto.UserDTO;
import kz.partnerservice.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PutMapping
    public UserDTO updateOne(@RequestBody @Valid UserDTO userDTO) throws CustomException {
        return userService.updateOne(userDTO);
    }

    @PostMapping("/image")
    public MediaFileDTO uploadImage(@RequestPart MultipartFile file) throws CustomException {
        return userService.uploadImage(file);
    }

    @GetMapping
    public UserDTO getInfo() throws CustomException {
        return userService.getInfo();
    }
}
