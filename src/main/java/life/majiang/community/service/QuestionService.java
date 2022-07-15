package life.majiang.community.service;

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
    String doPublish(String title, String description, String tag, HttpServletRequest request, Model model);

    //首页问题列表功能：查询获得所有QuestionDTO (所有question信息和对应的user信息)
    List<QuestionDTO> list();
}
