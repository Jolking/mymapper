package com.mymapper.util;

import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.List;

/**
 * Created by huang on 3/16/16.
 */
public class Logger {
    private org.slf4j.Logger logger;

    private Logger(Class c) {
        logger = LoggerFactory.getLogger(c);
    }

    public static Logger newInstance(Class c) {
        return new Logger(c);
    }

    public void debug(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message == null ? null : message);
        }
    }

    public void info(Object... messages) {
        if (logger.isInfoEnabled()) {
            for (Object m : messages) {
                logger.info(m == null ? null : m.toString());
            }
        }
    }

    public void info(Enumeration<?> messagesEnum) {
        if (logger.isInfoEnabled()) {
            while (messagesEnum.hasMoreElements())
                logger.info(messagesEnum.nextElement().toString());
        }
    }

    public void warn(String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(message == null ? null : message);
        }
    }

    public void error(String message, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(message == null ? null : message, e);
        }
    }

    public void trace(String message) {
        if (logger.isTraceEnabled()) {
            logger.trace(message == null ? null : message);
        }
    }

    public void listInfo(List list) {
        if (logger.isInfoEnabled() && null != list) {
            list.forEach(obj -> info(obj));
        }
    }
}
