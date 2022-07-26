package life.majiang.community.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import life.majiang.community.cache.TagCache;
import life.majiang.community.dao.QuestionDao;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.Question;
import life.majiang.community.domain.User;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.enums.CustomizeStatusCode;
import life.majiang.community.service.CommentService;
import life.majiang.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private CommentService commentService;

    //发布问题功能
    @Override
    public String doPublish(String title, String description, String tag, Integer id, HttpServletRequest request, Model model) {
        //页面传递了title、description、tag，还需要获取creator

        //即使有异常，也能拿到输入的信息，在publish页面继续回显信息
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        //把标签缓存发送给前端展示
        model.addAttribute("tags", TagCache.get());

        //点击发布，如果信息为空时，给出提示
        if (StringUtils.isBlank(title)){
            model.addAttribute("error","标题不能为空");
            if (id != null){
                //当id不为空，此时在编辑问题，需要再次发送id给前端，给出提示后，重新提交表单时就会把id也传给后端
                //否则给出提示后，重新提交表单会 发布新问题 而不是 更新问题
                model.addAttribute("id",id);
            }
            return "publish";//有异常，则跳转回publish页面
        }
        if (StringUtils.isBlank(description)){
            model.addAttribute("error","问题补充不能为空");
            if (id != null){
                //当id不为空，此时在编辑问题，需要再次发送id给前端，给出提示后，重新提交表单时就会把id也传给后端
                //否则给出提示后，重新提交表单会 发布新问题 而不是 更新问题
                model.addAttribute("id",id);
            }
            return "publish";//有异常，则跳转回publish页面
        }
        if (StringUtils.isBlank(tag)){
            model.addAttribute("error","标签不能为空");
            if (id != null){
                //当id不为空，此时在编辑问题，需要再次发送id给前端，给出提示后，重新提交表单时就会把id也传给后端
                //否则给出提示后，重新提交表单会 发布新问题 而不是 更新问题
                model.addAttribute("id",id);
            }
            return "publish";//有异常，则跳转回publish页面
        }
        //非法标签检测
        String invalid = TagCache.filterInvalid(tag);
        //这里反而不能用StringUtils.isNotBlank()方法，否则对于空白符"  "会返回false
        //而是应该只要 非法标签 不为null且不为"" 就认为存在非法标签
        if (invalid != null && !invalid.isEmpty()){
            model.addAttribute("error","检测到非法标签:" + invalid);
            if (id != null){
                //当id不为空，此时在编辑问题，需要再次发送id给前端，给出提示后，重新提交表单时就会把id也传给后端
                //否则给出提示后，重新提交表单会 发布新问题 而不是 更新问题
                model.addAttribute("id",id);
            }
            return "publish";
        }

        //关于creator的获取
        //思路：访问页面时，拦截器进行持久化登录状态，其中会获取cookie，用里面的token查询数据库获得user并写入session
        //          ---> 只需获取session里面的user，里面的account_id就是creator
        //可能的不足：session默认无操作30分钟就会销毁，而cookie能保持更长时间
        User userByToken = (User) request.getSession().getAttribute("user");
        //如果这个userByToken用户不存在，给出提示信息
        if (userByToken == null){
            model.addAttribute("error","用户未登录");
            return "publish";//有异常，则跳转回publish页面
        }

        //用户存在，则需要验证问题是否已经存在
        //根据传进来的id验证，存在则更新(update)，不存在则新增(insert)
        if (id != null){
            //问题已经存在，更新问题
            Question question = questionDao.selectById(id);//查询获取已经存在的问题

            question.setTitle(title);
            question.setDescription(description);
            question.setTag(tag);
            question.setGmtModified(System.currentTimeMillis());

            int update = questionDao.updateById(question);//更新问题
            //异常处理：更新问题之前，问题可能已被删除
            if (update != 1){
                throw new CustomizeException(CustomizeStatusCode.QUESTION_NOT_FOUND);
            }
        }else {
            //问题不存在，新增问题
            Question question = new Question();
            question.setTitle(title);
            question.setDescription(description);
            question.setTag(tag);
            question.setCreator(userByToken.getAccountId());//设置creator
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());

            questionDao.insert(question);//用MyBatis-Plus自带的insert，null的属性不会设置
        }

        return "redirect:/";//没有异常，则跳转回(重定向)首页
    }

    //首页问题列表功能(带有分页功能，按时间倒序)
    @Override
    public PaginationDTO<QuestionDTO> list(Integer currentPage, Integer pageSize) {
        //分页查询之前，要防止页面url传递的currentPage超过总页数，导致分页查询结果为空
        Integer totalCount = questionDao.selectCount(null);//用MyBatis-Plus自带的查询总数
        int totalPage = 0;
        if (totalCount % pageSize == 0){
            totalPage = totalCount / pageSize;
        }else {
            totalPage = (totalCount / pageSize) + 1;
        }
        if (currentPage < 1){
            currentPage = 1;
        }
        if (currentPage > totalPage){
            currentPage = totalPage;
        }
        //使用MyBatis-Plus自带的分页查询 获取 当前页所有question(按时间倒序)
        IPage<Question> page = new Page<>(currentPage,pageSize);
        LambdaQueryWrapper<Question> lqwQuestion = new LambdaQueryWrapper<>();
        lqwQuestion.orderByDesc(Question::getGmtCreate);//按时间倒序
        questionDao.selectPage(page, lqwQuestion);
        List<Question> currentPageQuestions = page.getRecords();

        List<QuestionDTO> questionDTOList = new ArrayList<>();//存放 当前页的 所有问题记录(所有QuestionDTO)

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();//代表当前页的PaginationDTO对象

        String tagBefore = null;
        for (Question question : currentPageQuestions) {
            //根据每一个question中的creator(也就是account_id)查询对应的user
            LambdaQueryWrapper<User> lqwUser = new LambdaQueryWrapper<>();
            lqwUser.eq(User::getAccountId,question.getCreator());//查询条件：where account_id = ?
            User user = userDao.selectOne(lqwUser);//用MyBatis-Plus自带的按条件查询

            //把每一个question信息和对应的user信息 放进 每一个questionDTO
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//利用工具类，快速复制question信息
            questionDTO.setUser(user);//设置user信息
            //处理tag之后再放进questionDTO
            tagBefore = question.getTag();
            String[] tagAfter = tagBefore.split("，|,");//以，或,分隔标签
            questionDTO.setTagList(Arrays.asList(tagAfter));

            //依次把 当前页的 所有问题记录(所有QuestionDTO) 放进 questionDTOList
            questionDTOList.add(questionDTO);
        }

        //设置 代表当前页的PaginationDTO对象 的各个属性，最后一个形参为 当前页码前后最多可展示的页码
        paginationDTO.setPagination(questionDTOList,page.getPages(),currentPage,3);

        return paginationDTO;
    }

    //个人中心展示我的问题(带有分页功能，按时间倒序)
    @Override
    public PaginationDTO<QuestionDTO> list(Integer creator, Integer currentPage, Integer pageSize) {
        //分页查询之前，要防止页面url传递的currentPage超过总页数，导致分页查询结果为空
        LambdaQueryWrapper<Question> lqwQuestion = new LambdaQueryWrapper<>();
        lqwQuestion.eq(Question::getCreator, creator);//查询creator对应的问题总数
        Integer totalCount = questionDao.selectCount(lqwQuestion);//用MyBatis-Plus自带的查询总数
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = (totalCount / pageSize) + 1;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > totalPage) {
            currentPage = totalPage;
        }
        //使用MyBatis-Plus自带的分页查询 获取 当前用户当前页所有question(按时间倒序)
        IPage<Question> page = new Page<>(currentPage, pageSize);
        lqwQuestion.orderByDesc(Question::getGmtCreate);//按时间倒序
        questionDao.selectPage(page, lqwQuestion);
        List<Question> currentPageMyQuestions = page.getRecords();

        List<QuestionDTO> myQuestionDTOList = new ArrayList<>();//存放 当前用户当前页的 所有问题记录(所有QuestionDTO)

        PaginationDTO<QuestionDTO> myPaginationDTO = new PaginationDTO<>();//代表当前用户当前页的PaginationDTO对象

        //根据creator查询当前用户信息
        LambdaQueryWrapper<User> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.eq(User::getAccountId, creator);
        User user = userDao.selectOne(lqwUser);

        String tagBefore = null;
        for (Question question : currentPageMyQuestions) {
            //把每一个question信息和当前user信息 放进 每一个questionDTO
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//利用工具类，快速复制question信息
            questionDTO.setUser(user);//设置user信息
            //处理tag之后再放进questionDTO
            tagBefore = StringUtils.replace(question.getTag(),"，",",");
            String[] tagAfter = StringUtils.split(tagBefore,",");//以,分隔标签
            questionDTO.setTagList(Arrays.asList(tagAfter));

            //依次把 当前用户当前页的 所有问题记录(所有QuestionDTO) 放进 questionDTOList
            myQuestionDTOList.add(questionDTO);
        }

        //设置 代表当前用户当前页的PaginationDTO对象 的各个属性，最后一个形参为 当前页码前后最多可展示的页码
        myPaginationDTO.setPagination(myQuestionDTOList, page.getPages(), currentPage, 3);

        return myPaginationDTO;
    }

    //问题详情功能(累加阅读数)
    @Override
    public String question(Integer id, Model model) {
        //查询获取当前问题
        Question question = questionDao.selectById(id);
        //异常处理：问题可能不存在(id不存在)
        if (question == null){
            throw new CustomizeException(CustomizeStatusCode.QUESTION_NOT_FOUND);
        }

        //更新阅读数
        //UPDATE question SET `view_count` = `view_count` + 1 WHERE (id = ?)
        LambdaUpdateWrapper<Question> luw = new LambdaUpdateWrapper<>();
        luw.eq(Question::getId,question.getId()).setSql("`view_count` = `view_count` + 1");
        questionDao.update(null,luw);

        //查询获取问题作者的用户信息
        LambdaQueryWrapper<User> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.eq(User::getAccountId,question.getCreator());
        User user = userDao.selectOne(lqwUser);

        //查询获取当前问题的最新状态(阅读数)
        Question latestQuestion = questionDao.selectById(id);

        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(latestQuestion,questionDTO);
        questionDTO.setUser(user);
        //处理tag之后再放进QuestionDTO
        String tagBefore = StringUtils.replace(question.getTag(),"，",",");
        String[] tagAfter = StringUtils.split(tagBefore,",");//以,分隔标签
        questionDTO.setTagList(Arrays.asList(tagAfter));


        //获取问题的回复列表(按时间倒序)
        List<CommentDTO> commentDTOList = commentService.list(id,CommentTypeEnum.QUESTION);

        //获取相关问题列表
        List<QuestionDTO> relatedQuestionDTOList = relatedQuestion(questionDTO);

        //发送给前端
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",commentDTOList);
        model.addAttribute("relatedQuestions",relatedQuestionDTOList);

        return "question";//跳转回question页面
    }

    //更新问题功能
    @Override
    public String edit(Integer id, Model model) {
        //查询获取当前问题
        Question question = questionDao.selectById(id);

        //将问题信息回显到发布页面
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());

        //返回问题id给发布页面，发布页面提交表单时会把id(有值或null)也提交，方便在doPublish方法中 更新/新增问题
        model.addAttribute("id",question.getId());

        //把标签缓存发送给前端展示
        model.addAttribute("tags", TagCache.get());

        return "publish";
    }

    //相关问题
    @Override
    public List<QuestionDTO> relatedQuestion(QuestionDTO questionDTO) {
        List<String> tagList = questionDTO.getTagList();
        //验证问题标签是否为空
        if (tagList == null || tagList.isEmpty()){
            return new ArrayList<>();
        }

        //问题标签不为空，则根据标签获取相关问题
        //用Java 8 Stream将所有tag拼接成一个字符串(以|分隔)，生成正则表达式
        String regexpTag = tagList.stream().collect(Collectors.joining("|"));

        LambdaQueryWrapper<Question> lqw = new LambdaQueryWrapper<>();
        //where id != questionDTO.getId() and tag regexp 'regexpTag'
        lqw.ne(Question::getId,questionDTO.getId()).apply("tag regexp '" + regexpTag + "'");
        List<Question> relatedQuestionList = questionDao.selectList(lqw);

        List<QuestionDTO> relatedQuestionDTOList = relatedQuestionList.stream().map(question -> {
            QuestionDTO relatedQuestionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, relatedQuestionDTO);
            //相关问题目前只需要id和标题，所以暂时不设置tag和user信息
            //点击 相关问题 跳转 问题详情页面时，需要用到id查询问题所有信息
            return relatedQuestionDTO;
        }).collect(Collectors.toList());

        return relatedQuestionDTOList;
    }
}
