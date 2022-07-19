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
    private Integer id;//用户id，唯一标识
    private String bio;//用户描述
    private String avatarUrl;//用户头像url地址
    //GitHubUser对象都是通过GitHubProvider中的getUser方法获取的
    // ---> 其中是通过GitHubUser gitHubUser = JSON.parseObject(string, GitHubUser.class);获取接口返回json数据
    //  ---> 但是在接收到的json数据中，头像url地址 是 avatar_url 而不是 avatarUrl
    //   ---> 通常需要以 同样的名字avatar_url 映射到 GitHubUser
    //    ---> 但fastjson的增强功能：会把带有下划线的json数据(avatar_url) 自动映射到 驼峰命名的属性(avatarUrl)
    //     ---> 所以可以直接定义为 private String avatarUrl;
}
