package life.majiang.community.service;

import life.majiang.community.dto.GitHubUser;

/**
 * @author CZS
 * @create 2022-07-13 22:56
 * <p>
 * 业务层接口：用户功能
 **/
public interface UserService {

    //新增或更新用户信息
    void createOrUpdate(GitHubUser gitHubUser, String token);
}
