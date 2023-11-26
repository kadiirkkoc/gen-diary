package com.gendiary.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class GenDiaryServerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 45487L;

    private HttpStatus errorCode;

    public GenDiaryServerException(){
        super("Unexpected Exception Encountered");
    }

    public GenDiaryServerException(String message, HttpStatus errorCode){
        super(message);
        this.errorCode=errorCode;
    }

    public HttpStatus getErrorCode() {
        return errorCode;
    }
}
