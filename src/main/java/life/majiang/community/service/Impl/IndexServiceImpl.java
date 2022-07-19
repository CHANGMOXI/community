package life.majiang.community.service.Impl;

import life.majiang.community.dao.UserDao;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.service.IndexService;
import life.majiang.community.service.QuestionService;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-14 12:30
 *
 * 首页功能业务
 *
 **/

@Service//业务层的组件，等价于@Component
public class IndexServiceImpl implements IndexService {
    @Autowired
    private QuestionService questionService;

    @Override
    public String index(HttpServletRequest request, Model model, Integer page, Integer size) {

        //*********************************** 首页问题列表、分页功能 ***********************************
        PaginationDTO paginationDTO = questionService.list(page,size);//获取当前页所有问题记录(包括头像url地址)
        model.addAttribute("pagination",paginationDTO);//把当前页所有问题记录发送给首页，首页进行显示
        //**************************************************************************************

        return "index";
    }
}
