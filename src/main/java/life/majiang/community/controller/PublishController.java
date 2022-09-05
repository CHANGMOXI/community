package life.majiang.community.controller;

import life.majiang.community.cache.TagCache;
import life.majiang.community.dao.QuestionDao;
import life.majiang.community.domain.Question;
import life.majiang.community.domain.User;
import life.majiang.community.enums.CustomizeStatusCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-13 11:34
 * <p>
 * 发布问题页面controller
 **/

@Controller
@RequestMapping("/publish")
public class PublishController {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public String publish(Model model) {
        //所有页面持久化登录状态 交给 拦截器

        //把标签缓存发送给前端展示
        model.addAttribute("tags", TagCache.get());

        return "publish";//跳转到publish页面
    }

    @GetMapping("/{id}")
    public String edit(@PathVariable(name = "id") Integer id, Model model) {
        //查询获取当前问题
        Question question = questionDao.selectById(id);
        //异常处理：问题可能已经被删除
        if (question == null) {
            throw new CustomizeException(CustomizeStatusCode.QUESTION_NOT_FOUND);
        }

        //将问题信息回显到发布页面
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());

        //返回问题id给发布页面，发布页面提交表单时会把id(有值或null)也提交，方便在doPublish方法中 更新/新增问题
        model.addAttribute("id", question.getId());

        //把标签缓存发送给前端展示
        model.addAttribute("tags", TagCache.get());

        return "publish";
    }

    @PostMapping
    public String doPublish(@RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "tag", required = false) String tag,
                            @RequestParam(value = "id", required = false) Integer id,
                            HttpServletRequest request, Model model) {
        //页面传递了title、description、tag，还需要获取creator

        //即使有异常，也能拿到输入的信息，在publish页面继续回显信息
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        //把标签缓存发送给前端展示
        model.addAttribute("tags", TagCache.get());

        //点击发布，如果信息为空时，给出提示
        if (StringUtils.isBlank(title)) {
            model.addAttribute("error", "标题不能为空");
            if (id != null) {
                //当id不为空，此时在编辑问题，需要再次发送id给前端，给出提示后，重新提交表单时就会把id也传给后端
                //否则给出提示后，重新提交表单会 发布新问题 而不是 更新问题
                model.addAttribute("id", id);
            }
            return "publish";//有异常，则跳转回publish页面
        }
        if (StringUtils.isBlank(description)) {
            model.addAttribute("error", "问题补充不能为空");
            if (id != null) {
                //当id不为空，此时在编辑问题，需要再次发送id给前端，给出提示后，重新提交表单时就会把id也传给后端
                //否则给出提示后，重新提交表单会 发布新问题 而不是 更新问题
                model.addAttribute("id", id);
            }
            return "publish";//有异常，则跳转回publish页面
        }
        if (StringUtils.isBlank(tag)) {
            model.addAttribute("error", "标签不能为空");
            if (id != null) {
                //当id不为空，此时在编辑问题，需要再次发送id给前端，给出提示后，重新提交表单时就会把id也传给后端
                //否则给出提示后，重新提交表单会 发布新问题 而不是 更新问题
                model.addAttribute("id", id);
            }
            return "publish";//有异常，则跳转回publish页面
        }
        //非法标签检测
        String invalid = TagCache.filterInvalid(tag);
        //这里反而不能用StringUtils.isNotBlank()方法，否则对于空白符"  "会返回false
        //而是应该只要 非法标签 不为null且不为"" 就认为存在非法标签
        if (invalid != null && !invalid.isEmpty()) {
            model.addAttribute("error", "检测到非法标签:" + invalid);
            if (id != null) {
                //当id不为空，此时在编辑问题，需要再次发送id给前端，给出提示后，重新提交表单时就会把id也传给后端
                //否则给出提示后，重新提交表单会 发布新问题 而不是 更新问题
                model.addAttribute("id", id);
            }
            return "publish";
        }

        //关于creator的获取
        //思路：访问页面时，拦截器进行持久化登录状态，其中会获取cookie，用里面的token查询数据库获得user并写入session
        //          ---> 只需获取session里面的user，里面的account_id就是creator
        //可能的不足：session默认无操作30分钟就会销毁，而cookie能保持更长时间
        User userByToken = (User) request.getSession().getAttribute("user");
        //如果这个userByToken用户不存在，给出提示信息
        if (userByToken == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";//有异常，则跳转回publish页面
        }

        //用户存在，则需要验证问题是否已经存在
        //根据传进来的id验证，存在则更新(update)，不存在则新增(insert)
        Question question = new Question();
        question.setId(id);
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(userByToken.getAccountId());

        questionService.createOrUpdate(question);//新增或更新问题

        return "redirect:/";//没有异常，则跳转回(重定向)首页
    }
}
