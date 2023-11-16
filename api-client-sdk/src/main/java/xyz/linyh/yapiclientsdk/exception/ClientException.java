package xyz.linyh.yapiclientsdk.exception;

public class ClientException extends RuntimeException{
    private final int code;

    public ClientException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ClientException(ClientErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public ClientException(ClientErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
