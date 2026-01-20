package com.soekm.abrs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author James Mwaura
 * 2026
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordLoginRequest {
    private String username;
    private String password;
}
