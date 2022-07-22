package life.majiang.community.dto;

import life.majiang.community.domain.User;
import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-20 12:22
 **/
@Data
public class CommentDTO {
    private Integer id;
    private Integer parentId;
    private Integer type;
    private Integer commentAuthor;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private Long gmtCreate;
    private Long gmtModified;

    private User user;
}
