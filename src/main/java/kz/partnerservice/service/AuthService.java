package kz.partnerservice.service;

import kz.partnerservice.exception.CustomException;
import kz.partnerservice.model.dto.AuthDTO;

public interface AuthService {

    void signUp(AuthDTO authDTO) throws CustomException;

    AuthDTO signIn(AuthDTO authDTO);
}
