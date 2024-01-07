package com.gendiary.loggers;

import com.gendiary.exception.GenDiaryServerException;
import com.gendiary.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

public class MainLogger {

    private final Logger logger;

    public MainLogger(Class<?> tClass){
        this.logger= LogManager.getLogger(tClass);
    }

    public Supplier<? extends User> log(String message){
        logger.info(message);
        return null;
    }

    public String log(String message, HttpStatus httpStatus){
        logger.error(message);
        throw new GenDiaryServerException(message,httpStatus);
    }

    public void error(String s) {
    }
}
