package life.majiang.community.service;

import life.majiang.community.domain.Question;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;

import java.util.List;

/**
 * @author CZS
 * @create 2022-07-13 22:57
 * <p>
 * 业务层接口：问题相关功能
 **/
public interface QuestionService {

    //新增或更新问题
    void createOrUpdate(Question question);

    //首页问题列表功能、搜索功能(带有分页功能，按时间倒序)
    PaginationDTO<QuestionDTO> list(String search, Integer currentPage, Integer pageSize);

    //重载list方法：个人中心展示我的问题(带有分页功能，按时间倒序)
    PaginationDTO<QuestionDTO> list(Integer creator, Integer currentPage, Integer pageSize);

    //问题详情功能
    QuestionDTO question(Integer id);

    //相关问题
    List<QuestionDTO> relatedQuestion(QuestionDTO questionDTO);

}
