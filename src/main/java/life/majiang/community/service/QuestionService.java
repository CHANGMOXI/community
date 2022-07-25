package life.majiang.community.service;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author CZS
 * @create 2022-07-13 22:57
 *
 * 业务层接口：文章发布功能
 *
 **/
public interface QuestionService {

    //发布问题功能
    String doPublish(String title, String description, String tag, Integer id, HttpServletRequest request, Model model);

    //首页问题列表功能(带有分页功能，按时间倒序)
    PaginationDTO list(Integer currentPage, Integer pageSize);

    //重载list方法：个人中心展示我的问题(带有分页功能，按时间倒序)
    PaginationDTO list(Integer creator, Integer currentPage, Integer pageSize);

    //问题详情功能
    String question(Integer id, Model model);

    //更新问题功能
    String edit(Integer id, Model model);

    //相关问题
    List<QuestionDTO> relatedQuestion(QuestionDTO questionDTO);

}
