package life.majiang.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import life.majiang.community.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author CZS
 * @create 2022-07-12 18:12
 **/

//用mybatis-plus
@Mapper
public interface UserDao extends BaseMapper<User> {
    //与整合MyBatis的不同：继承BaseMapper，指定泛型后，就带有基本的CURD操作的方法
}
