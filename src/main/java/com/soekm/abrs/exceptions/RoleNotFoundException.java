package com.soekm.abrs.exceptions;

/**
 * @author James Mwaura
 * 2026
 */

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}