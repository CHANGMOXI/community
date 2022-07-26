package life.majiang.community.dto;

import life.majiang.community.domain.User;
import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-26 11:28
 **/
@Data
public class NotificationDTO {
    private Integer id;
    private Long gmtCreate;
    private Integer status;

    private Integer sender;//发送者id
    private String senderName;//发送者名字
    private Integer parentId;//父类id
    private String parentTitle;//父类问题/评论的标题/内容
    private Integer type;

    private String desc;//通知类型的描述：回复了问题/评论
    //对应NotificationTypeEnum的desc属性，根据Notification的type属性来设置
}
