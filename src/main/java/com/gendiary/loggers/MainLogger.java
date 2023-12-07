package com.gendiary.loggers;

import com.gendiary.exception.GenDiaryServerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

public class MainLogger {

    private final Logger logger;

    public MainLogger(Class<?> tClass){
        this.logger= LogManager.getLogger(tClass);
    }

    public void log(String message){
        logger.info(message);
    }

    public void log(String message, HttpStatus httpStatus){
        logger.error(message);
        throw new GenDiaryServerException(message,httpStatus);
    }
}
