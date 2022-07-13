package life.majiang.community.service;

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

    //
    String doPublish(String title, String description, String tag, HttpServletRequest request, Model model);
}
