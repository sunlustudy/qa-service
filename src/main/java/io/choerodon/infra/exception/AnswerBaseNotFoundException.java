package io.choerodon.infra.exception;


public class AnswerBaseNotFoundException extends RuntimeException {

    public AnswerBaseNotFoundException() {
    }

    public AnswerBaseNotFoundException(String message) {
        super(message);
    }

    public AnswerBaseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnswerBaseNotFoundException(Throwable cause) {
        super(cause);
    }

    public AnswerBaseNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

