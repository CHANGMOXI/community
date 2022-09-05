package life.majiang.community.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import life.majiang.community.dao.QuestionDao;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.Question;
import life.majiang.community.domain.User;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.enums.CustomizeStatusCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CZS
 * @create 2022-07-13 22:59
 * <p>
 * 发布文章功能业务
 **/

@Service//业务层的组件，等价于@Component
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    //新增或更新问题
    @Override
    public void createOrUpdate(Question question) {
        //根据传进来的id验证，存在则更新(update)，不存在则新增(insert)
        if (question.getId() != null) {
            Question dbQuestion = questionDao.selectById(question.getId());//查询获取已经存在的问题
            //异常处理：查询问题之前，问题可能已经被删除
            if (dbQuestion == null) {
                throw new CustomizeException(CustomizeStatusCode.QUESTION_NOT_FOUND);
            }

            //问题已经存在，更新问题
            dbQuestion.setTitle(question.getTitle());
            dbQuestion.setDescription(question.getDescription());
            dbQuestion.setTag(question.getTag());
            dbQuestion.setGmtModified(System.currentTimeMillis());

            int update = questionDao.updateById(dbQuestion);//更新问题
            //异常处理：更新问题之前，问题可能已被删除
            if (update != 1) {
                throw new CustomizeException(CustomizeStatusCode.QUESTION_NOT_FOUND);
            }
        } else {
            //问题不存在，新增问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());

            questionDao.insert(question);//用MyBatis-Plus自带的insert，null的属性不会设置
        }
    }

    //首页问题列表功能、搜索功能(带有分页功能，按时间倒序)
    @Override
    public PaginationDTO<QuestionDTO> list(String search, Integer currentPage, Integer pageSize) {
        //验证搜索内容
        if (StringUtils.isNotBlank(search)) {
            String[] split = StringUtils.split(search, " ");
            search = Arrays.stream(split).collect(Collectors.joining("|"));
        } else {
            //search没有内容，设置为null，防止search为 ""空 或 "  "空白符 的情况也传进countBySearch方法中
            search = null;
        }

        //分页查询之前，要防止页面url传递的currentPage超过总页数，导致分页查询结果为空
        Integer totalCount = questionDao.countBySearch(search);//用XML自定义动态SQL
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
        //使用MyBatis-Plus自带的分页查询
        //根据search是否有内容，获取 当前页所有question 或 当前页所有有关search的question(按时间倒序)
        IPage<Question> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Question> lqwQuestion = new LambdaQueryWrapper<>();
        if (search != null) {
            //search有内容，则查询有关搜索内容的问题
            lqwQuestion.apply("title regexp '" + search + "'");
        }
        lqwQuestion.orderByDesc(Question::getGmtCreate);//按时间倒序
        questionDao.selectPage(page, lqwQuestion);
        List<Question> currentPageQuestions = page.getRecords();

        List<QuestionDTO> questionDTOList = new ArrayList<>();//存放 当前页的 所有问题记录(所有QuestionDTO)

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();//代表当前页的PaginationDTO对象

        String tagBefore = null;
        for (Question question : currentPageQuestions) {
            //根据每一个question中的creator(也就是account_id)查询对应的user
            LambdaQueryWrapper<User> lqwUser = new LambdaQueryWrapper<>();
            lqwUser.eq(User::getAccountId, question.getCreator());//查询条件：where account_id = ?
            User user = userDao.selectOne(lqwUser);//用MyBatis-Plus自带的按条件查询

            //把每一个question信息和对应的user信息 放进 每一个questionDTO
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//利用工具类，快速复制question信息
            questionDTO.setUser(user);//设置user信息
            //处理tag之后再放进questionDTO
            tagBefore = StringUtils.replace(question.getTag(), "，", ",");
            String[] tagAfter = StringUtils.split(tagBefore, ",");//以,分隔标签
            questionDTO.setTagList(Arrays.asList(tagAfter));

            //依次把 当前页的 所有问题记录(所有QuestionDTO) 放进 questionDTOList
            questionDTOList.add(questionDTO);
        }

        //设置 代表当前页的PaginationDTO对象 的各个属性，最后一个形参为 当前页码前后最多可展示的页码
        paginationDTO.setPagination(questionDTOList, page.getPages(), currentPage, 3);

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
            tagBefore = StringUtils.replace(question.getTag(), "，", ",");
            String[] tagAfter = StringUtils.split(tagBefore, ",");//以,分隔标签
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
    public QuestionDTO question(Integer id) {
        //查询获取当前问题
        Question question = questionDao.selectById(id);
        //异常处理：问题可能不存在(id不存在)
        if (question == null) {
            throw new CustomizeException(CustomizeStatusCode.QUESTION_NOT_FOUND);
        }

        //更新阅读数
        //UPDATE question SET `view_count` = `view_count` + 1 WHERE (id = ?)
        LambdaUpdateWrapper<Question> luw = new LambdaUpdateWrapper<>();
        luw.eq(Question::getId, question.getId()).setSql("`view_count` = `view_count` + 1");
        questionDao.update(null, luw);

        //查询获取问题作者的用户信息
        LambdaQueryWrapper<User> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.eq(User::getAccountId, question.getCreator());
        User user = userDao.selectOne(lqwUser);

        //查询获取当前问题的最新状态(阅读数)
        Question latestQuestion = questionDao.selectById(id);

        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(latestQuestion, questionDTO);
        questionDTO.setUser(user);

        //处理tag之后再放进QuestionDTO
        String tagBefore = StringUtils.replace(question.getTag(), "，", ",");
        String[] tagAfter = StringUtils.split(tagBefore, ",");//以,分隔标签
        questionDTO.setTagList(Arrays.asList(tagAfter));

        return questionDTO;
    }

    //相关问题
    @Override
    public List<QuestionDTO> relatedQuestion(QuestionDTO questionDTO) {
        List<String> tagList = questionDTO.getTagList();
        //验证问题标签是否为空
        if (tagList == null || tagList.isEmpty()) {
            return new ArrayList<>();
        }

        //问题标签不为空，则根据标签获取相关问题
        //用Java 8 Stream将所有tag拼接成一个字符串(以|分隔)，生成正则表达式
        String regexpTag = tagList.stream().collect(Collectors.joining("|"));

        LambdaQueryWrapper<Question> lqw = new LambdaQueryWrapper<>();
        //where id != questionDTO.getId() and tag regexp 'regexpTag'
        lqw.ne(Question::getId, questionDTO.getId()).apply("tag regexp '" + regexpTag + "'");
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
