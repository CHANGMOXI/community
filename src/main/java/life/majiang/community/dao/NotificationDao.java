package life.majiang.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import life.majiang.community.domain.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author CZS
 * @create 2022-07-25 22:54
 **/
//用MyBatis-Plus
@Mapper
public interface NotificationDao extends BaseMapper<Notification> {
    //与整合MyBatis的不同：继承BaseMapper，指定泛型后，就带有基本的CURD操作的方法
    //需要定制CRUD，比如按条件查询，可以用LambdaQueryWrapper定制查询条件
}
