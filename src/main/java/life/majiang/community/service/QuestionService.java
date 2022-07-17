package life.majiang.community.service;

import life.majiang.community.dto.PaginationDTO;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-13 22:57
 *
 * 业务层接口：文章发布功能
 *
 **/
public interface QuestionService {

    //发布问题功能
    String doPublish(String title, String description, String tag, HttpServletRequest request, Model model);

    //首页问题列表功能(带有分页功能)
    PaginationDTO list(Integer currentPage, Integer pageSize);
}
