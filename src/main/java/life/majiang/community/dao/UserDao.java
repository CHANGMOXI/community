package life.majiang.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import life.majiang.community.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author CZS
 * @create 2022-07-12 18:12
 **/

//用mybatis-plus
@Mapper
public interface UserDao extends BaseMapper<User> {
    //与整合MyBatis的不同：继承BaseMapper，指定泛型后，就带有基本的CURD操作的方法

    //自定义查询
    @Select("select * from user where token = #{token};")
    User selectByToken(@Param("token") String token);

    //自定义查询：根据question中的creator查询对应的user
    @Select("select * from user where account_id = #{accountId};")
    User selectByAccountId(@Param("accountId") Integer creator);
}
