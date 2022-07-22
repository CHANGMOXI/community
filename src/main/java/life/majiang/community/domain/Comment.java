package life.majiang.community.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-12 18:19
 **/

@Data
public class Comment {
    //指定MyBatis-Plus的id生成策略为 自增，否则使用MP自带的insert时，id为雪花算法生成的很长的数字
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer parentId;
    private Integer type;
    private Integer commentAuthor;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private Long gmtCreate;
    private Long gmtModified;

}
