package com.upensingh009.fidovalidation.core.validation;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;

public interface Validator<T> {
    ValidationResult validate(T input);
}
