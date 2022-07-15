package life.majiang.community.service.Impl;

import life.majiang.community.dao.QuestionDao;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.Question;
import life.majiang.community.domain.User;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CZS
 * @create 2022-07-13 22:59
 *
 * 发布文章功能业务
 *
 **/

@Service//业务层的组件，等价于@Component
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    //发布问题功能
    @Override
    public String doPublish(String title, String description, String tag, HttpServletRequest request, Model model) {
        //页面传递了title、description、tag，还需要获取creator

        //即使有异常，也能拿到输入的信息，在publish页面继续回显信息
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        //点击发布，如果信息为空时，给出提示
        if (title == null || title == ""){
            model.addAttribute("error","标题不能为空");
            return "publish";//有异常，则跳转回publish页面
        }
        if (description == null || description == ""){
            model.addAttribute("error","问题补充不能为空");
            return "publish";//有异常，则跳转回publish页面
        }
        if (tag == null || tag == ""){
            model.addAttribute("error","标签不能为空");
            return "publish";//有异常，则跳转回publish页面
        }

        //关于creator的获取
        //1.原本思路：像持久化登录状态一样，获取cookie，用里面的token查询数据库获得user，user里的account_id也就是creator
        //2.我的思路：直接从session里面获取gitHubUser的id，也就是creator
        //      ---> 在UserServiceImpl的loginByGitHub方法中，首次登录第一次写session时，就把giHubUser写入session，里面就有用户id
        //       ---> 可能的不足：session默认无操作30分钟就会销毁，而cookie能保持更长时间

        //获取creator原本思路
        User userByToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length >0){  //判断cookie有没有内容
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    userByToken = userDao.selectByToken(token);//根据token去数据库查询对应用户

                    if (userByToken != null){
                        //如果这个userByToken用户存在，则写入到session
                        request.getSession().setAttribute("user",userByToken);
                    }
                    break;
                }
            }
        }
        //如果这个userByToken用户不存在，给出提示信息
        if (userByToken == null){
            model.addAttribute("error","用户未登录");
            return "publish";//有异常，则跳转回publish页面
        }

        //用户存在且绑定到session后，将文章信息存入数据库(表question)
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(userByToken.getAccountId());//设置creator
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());

        questionDao.save(question);//存入表question

        return "redirect:/";//没有异常，则跳转回(重定向)首页
    }


    //首页问题列表功能：查询获得所有QuestionDTO (所有question信息和对应的user信息)
    @Override
    public List<QuestionDTO> list() {
        //先查询获取所有question
        List<Question> questionList = questionDao.selectList(null);//使用MyBatis-Plus自带的查询

        ArrayList<QuestionDTO> questionDTOArrayList = new ArrayList<>();

        for (Question question : questionList) {
            //根据question中的creator(也就是account_id)查询对应的user
            User user = userDao.selectByAccountId(question.getCreator());

            //把question信息和对应的user信息放进questionDTO
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//利用工具类，快速复制question信息
            questionDTO.setUser(user);//设置user信息

            //一条问题记录 作为 一个questionDTO对象，放进list中
            questionDTOArrayList.add(questionDTO);
        }

        return questionDTOArrayList;
    }
}
