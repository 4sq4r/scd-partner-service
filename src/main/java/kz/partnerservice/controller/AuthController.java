package kz.partnerservice.controller;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.AuthDTO;
import kz.partnerservice.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl service;

    @PostMapping
    public void signUp(@RequestBody AuthDTO authDTO) throws CustomException {
        service.signUp(authDTO);
    }

    @PostMapping("/signIn")
    public AuthDTO signIn(@RequestBody AuthDTO authDTO) {
        return service.signIn(authDTO);
    }
}
