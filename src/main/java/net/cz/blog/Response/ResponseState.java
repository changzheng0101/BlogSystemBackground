package net.cz.blog.Response;

public enum ResponseState {
    SUCCESS(true, 200, "获取成功"),
    JOIN_IN_SUCCESS(true, 202, "注册成功"),
    FAILED(false, 400, "获取失败"),
    ACCOUNT_NOT_LOGIN(false, 402, "账号未登录"),
    PERMISSION_FORBID(false, 403, "无权限"),
    ACCOUNT_DENY(false, 405, "账号被加入黑名单"),
    Error_403(false, 406, "权限不足"),
    Error_404(false, 407, "页面丢失"),
    Error_504(false, 408, "系统繁忙，请稍后重试"),
    Error_505(false, 409, "请求错误，请查看提交参数");

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
