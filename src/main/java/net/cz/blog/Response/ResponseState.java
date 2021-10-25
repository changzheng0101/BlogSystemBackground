package net.cz.blog.Response;

public enum ResponseState {
    SUCCESS(true, 200, "获取成功"),
    FAILED(false, 400, "获取失败");

    private ResponseState(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    private boolean isSuccess;
    private int code;
    private String message;


    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
