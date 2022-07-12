package life.majiang.community.dto;

import lombok.Data;

/**
 * @author CZS
 * @create 2022-07-11 23:25
 **/
@Data
public class GitHubUser {
    //属性名 与 返回的user信息的属性名 要一致
    private String name;//用户名
    private Long id;//用户id，唯一标识
    private String bio;//用户描述
}
