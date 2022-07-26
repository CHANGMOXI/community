package life.majiang.community.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-25 22:52
 **/

@Data
public class Notification {
    //指定MyBatis-Plus的id生成策略为 自增，否则使用MP自带的insert时，id为雪花算法生成的很长的数字
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer sender;
    private String senderName;//发送者名字，提前缓存好，减少查询user表的次数
    private Integer receiver;
    private Integer parentId;
    private String parentTitle;//父类问题/评论的标题/内容，提前缓存好，减少查询question表/comment表的次数
    private Integer type;
    private Long gmtCreate;
    private Integer status;
}
