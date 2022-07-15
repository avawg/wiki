package org.wg.wiki.model.resp;

/**
 * 包装返回数据
 */
public class Result<T> {

    /**
     * 状态
     */
    private boolean success;

    /**
     * error msg
     */
    private String message;

    /**
     * 业务数据
     */
    private T data;

    public static Result success() {
        Result result = new Result();
        result.success = true;
        return result;
    }

    public static Result success(Object object) {
        Result result = new Result();
        result.success = true;
        result.data = object;
        return result;
    }

    public static Result error(String message) {
        Result result = new Result();
        result.success = false;
        result.message = message;
        return result;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ResponseDto{");
        sb.append("success=").append(success);
        sb.append(", message='").append(message).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
