package life.majiang.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import life.majiang.community.domain.Question;
import life.majiang.community.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author CZS
 * @create 2022-07-12 18:12
 **/

@Mapper
public interface QuestionDao extends BaseMapper<Question> {

    //自定义查询
    @Insert("insert into question (title,description,creator,tag,gmt_create,gmt_modified) values (#{title},#{description},#{creator},#{tag},#{gmtCreate},#{gmtModified});")
    void save(Question question);
}
