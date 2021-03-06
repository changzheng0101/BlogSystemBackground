package net.cz.blog.Response;

public class ResponseResult {
    private boolean isSuccess;
    private int statusCode;
    private String message;
    private Object data;

    public ResponseResult(ResponseState state) {
        this.isSuccess = state.isSuccess();
        this.statusCode = state.getCode();
        this.message = state.getMessage();
    }

    public static ResponseResult GET(ResponseState state) {
        return new ResponseResult(state);
    }

    public static ResponseResult SUCCESS() {
        return new ResponseResult(ResponseState.SUCCESS);
    }

    public static ResponseResult SUCCESS(String message) {
        ResponseResult responseResult = new ResponseResult(ResponseState.SUCCESS);
        responseResult.setMessage(message);
        return responseResult;
    }

    public static ResponseResult ACCOUNT_DENY() {
        return new ResponseResult(ResponseState.ACCOUNT_DENY);
    }

    public static ResponseResult Error_403() {
        return new ResponseResult(ResponseState.Error_403);
    }

    public static ResponseResult Error_404() {
        return new ResponseResult(ResponseState.Error_404);
    }

    public static ResponseResult Error_504() {
        return new ResponseResult(ResponseState.Error_504);
    }

    public static ResponseResult Error_505() {
        return new ResponseResult(ResponseState.Error_505);
    }

    public static ResponseResult ACCOUNT_NOT_LOGIN() {
        return new ResponseResult(ResponseState.ACCOUNT_NOT_LOGIN);
    }

    public static ResponseResult PERMISSION_FORBID() {
        return new ResponseResult(ResponseState.PERMISSION_FORBID);
    }

    public static ResponseResult FAILED() {
        return new ResponseResult(ResponseState.FAILED);
    }

    public static ResponseResult FAILED(String message) {
        ResponseResult responseResult = new ResponseResult(ResponseState.FAILED);
        responseResult.setMessage(message);
        return responseResult;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }
}
