package life.majiang.community.dto;

import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-27 21:24
 * <p>
 * 图片上传数据格式
 **/
@Data
public class FileDTO {
    private Integer success;// 0 表示上传失败，1 表示上传成功
    private String message;//提示的信息，上传成功或上传失败及错误信息等
    private String url;//图片地址，上传成功时才返回
}
