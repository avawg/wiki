package org.wg.wiki.exception;

public class BusinessException extends RuntimeException {

    private BusinessExceptionCode code;

    public BusinessException(BusinessExceptionCode code) {
        this.code = code;
    }

    public BusinessExceptionCode getCode() {
        return code;
    }

    public void setCode(BusinessExceptionCode code) {
        this.code = code;
    }

    /**
     * 不写入堆栈信息，提高性能
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
