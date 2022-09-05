package life.majiang.community.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.User;
import life.majiang.community.dto.GitHubUser;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CZS
 * @create 2022-07-13 22:59
 * <p>
 * 登录功能业务
 **/

@Service//业务层的组件，等价于@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public void createOrUpdate(GitHubUser gitHubUser, String token) {
        //GitHub返回的用户信息存在，登录成功

        //根据 gitHubUser中的用户id 查询用户信息 是否已经存在于 数据库的表user
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getAccountId, gitHubUser.getId());//查询条件：where account_id = ?
        User userByAccountId = userDao.selectOne(lqw);//用MyBatis-Plus自带的按条件查询

        if (userByAccountId != null) {
            //用户信息已存在，只更新字段name、token、gmt_modified、bio、avatar_url
            userByAccountId.setName(gitHubUser.getName());
            userByAccountId.setToken(token);//把上面生成的新token也放进去
            userByAccountId.setGmtModified(System.currentTimeMillis());
            userByAccountId.setBio(gitHubUser.getBio());
            userByAccountId.setAvatarUrl(gitHubUser.getAvatarUrl());//更新用户头像url地址

            userDao.updateById(userByAccountId);//更新对应的用户信息(用MyBatis-Plus自带的)
        } else {
            //用户信息不存在，将新的用户信息写入数据库
            userByAccountId = new User();
            userByAccountId.setName(gitHubUser.getName());
            userByAccountId.setAccountId(gitHubUser.getId());
            userByAccountId.setToken(token);//把上面生成的新token也放进去
            userByAccountId.setGmtCreate(System.currentTimeMillis());
            userByAccountId.setGmtModified(userByAccountId.getGmtCreate());//新用户gmtModified与gmtCreate一致
            userByAccountId.setBio(gitHubUser.getBio());
            userByAccountId.setAvatarUrl(gitHubUser.getAvatarUrl());//设置用户头像url地址

            userDao.insert(userByAccountId);//新用户信息存入数据库(用MyBatis-Plus自带的)
        }
    }
}

