package com.dfxsurvey.dfxsurvey.validators;

public interface Validator<T> {
    void validate(T target);
}
