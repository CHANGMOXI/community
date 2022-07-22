package life.majiang.community.dto;

import life.majiang.community.exception.CustomizeException;
import life.majiang.community.enums.CustomizeStatusCode;
import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-20 15:29
 *
 **/
@Data
public class ResultDTO<T> {
    Integer code;
    String msg;
    private T data;//使用泛型，这样data可以是集合类型 或 自定义类型(比如CommentDTO)

    public ResultDTO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultDTO(CustomizeStatusCode errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMessage();
    }

    public ResultDTO(CustomizeException e) {
        this.code = e.getCode();
        this.msg = e.getMessage();
    }

    public ResultDTO(T data) {
        this.code = CustomizeStatusCode.SUCCESS.getCode();
        this.msg = CustomizeStatusCode.SUCCESS.getMessage();
        this.data = data;
    }
}
