package life.majiang.community.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-12 18:19
 **/

@Data
public class Question {
    //指定mybatis-plus的id生成策略为 自增，否则使用MP自带的insert时，id为雪花算法生成的很长的数字
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;
    private String description;
    private String creator;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
}
