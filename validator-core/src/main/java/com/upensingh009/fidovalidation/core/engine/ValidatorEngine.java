package com.upensingh009.fidovalidation.core.engine;

import com.upensingh009.fidovalidation.common.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple ValidatorEngine that collects results from a list of validators executed externally.
 * In this minimal implementation the engine provides a convenience to aggregate results.
 */
public class ValidatorEngine {
    private final List<ValidationResult> results = new ArrayList<>();

    public void addResult(ValidationResult r) {
        results.add(r);
    }

    public List<ValidationResult> getResults() {
        return List.copyOf(results);
    }

    public boolean allPassed() {
        return results.stream().allMatch(r -> r.status().name().equals("PASS"));
    }
}
