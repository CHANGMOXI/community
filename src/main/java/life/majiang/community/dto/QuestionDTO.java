package life.majiang.community.dto;

import life.majiang.community.domain.User;
import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-14 12:45
 *
 * Question实体类 与 数据库中表的字段 关联，所以不能直接加上user属性(为了获取头像url地址等信息)
 *  ---> 通过 传输层DTO 实现，创建QuestionDTO，里面除了Question原本的属性(字段)，再加上 user属性
 *
 **/

@Data
public class QuestionDTO {
    private Integer id;
    private String title;
    private String description;
    private Integer creator;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;

    //首页问题列表功能中，因为Question不能直接加上user属性(为了获取头像url地址等信息)
    // ---> 通过用户account_id查询出的用户信息(包括头像url地址等)，将放在DTO这里
    private User user;
}
