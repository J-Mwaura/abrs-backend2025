package com.soekm.abrs.dto.response;

import java.time.ZonedDateTime;

/**
 * @author James Mwaura
 * 2026
 */

public record ApiErrorResponse(ZonedDateTime timestamp,
                               int status,
                               String error,
                               String message,
                               String path) {
}
