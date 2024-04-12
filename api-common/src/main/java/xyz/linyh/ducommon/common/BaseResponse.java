package xyz.linyh.ducommon.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用返回类
 *
 * @param <T>
 *
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseResponse<?> that = (BaseResponse<?>) o;

        if (code != that.code) return false;
        if (!Objects.equals(data, that.data)) return false;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
