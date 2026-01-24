package com.soekm.abrs.security.iService;

import com.soekm.abrs.dto.PasswordLoginRequest;
import com.soekm.abrs.dto.PinLoginRequest;
import com.soekm.abrs.dto.RegistrationRequest;
import com.soekm.abrs.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author James Mwaura
 * 2026
 */

public interface IAuthService {

    Map<String, Object> authenticateWithPassword(PasswordLoginRequest request);

    Map<String, Object> authenticateWithPin(PinLoginRequest request);

    ResponseEntity<ApiResponse<Map<String, Object>>> register(RegistrationRequest request);
}
