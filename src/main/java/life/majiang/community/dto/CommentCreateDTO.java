package life.majiang.community.dto;

import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-20 12:22
 **/
@Data
public class CommentCreateDTO {
    private Integer parentId;
    private Integer type;
    private String content;
}
