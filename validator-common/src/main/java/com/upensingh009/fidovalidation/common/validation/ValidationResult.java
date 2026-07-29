package com.upensingh009.fidovalidation.common.validation;

import java.time.Duration;

public record ValidationResult(String name,
                               ValidationStatus status,
                               String expected,
                               String actual,
                               String message,
                               Duration duration) {
}
