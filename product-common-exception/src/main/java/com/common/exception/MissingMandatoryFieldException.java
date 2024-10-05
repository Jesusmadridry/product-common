package com.common.exception;

import lombok.Getter;

@Getter
public class MissingMandatoryFieldException extends Exception {
    public MissingMandatoryFieldException(String errorMessage) {
        super(errorMessage);
    }
}
