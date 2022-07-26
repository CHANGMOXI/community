package life.majiang.community.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import life.majiang.community.dao.CommentDao;
import life.majiang.community.dao.NotificationDao;
import life.majiang.community.dao.QuestionDao;
import life.majiang.community.dao.UserDao;
import life.majiang.community.domain.Comment;
import life.majiang.community.domain.Notification;
import life.majiang.community.domain.Question;
import life.majiang.community.domain.User;
import life.majiang.community.dto.CommentCreateDTO;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.enums.NotificationStatusEnum;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.enums.CustomizeStatusCode;
import life.majiang.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CZS
 * @create 2022-07-14 12:30
 *
 * 回复功能业务
 *
 **/

@Service//业务层的组件，等价于@Component
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private NotificationDao notificationDao;

    //回复功能
    @Transactional//加入事务，当更新回复数的SQL语句执行失败时，会回滚新增问题评论的操作(同成功同失败)
    @Override
    public Object postComment(CommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        //需要登录才能评论
        User commentAuthor = (User) request.getSession().getAttribute("user");
        if (commentAuthor == null){
            return new ResultDTO(CustomizeStatusCode.NO_LOGIN);
        }
        //验证回复内容是否为空
        //这里判断是否为空(包括空白符)，使用commons-lang3的工具类StringUtils的isBlank()方法
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())){
            return new ResultDTO(CustomizeStatusCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setCommentAuthor(commentAuthor.getAccountId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());

        //异常处理：新增评论前，验证 回复的问题或评论是否存在、回复类型是否符合要求
        if (comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeStatusCode.QUESTION_OR_COMMENT_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeStatusCode.TYPE_PARAM_WRONG);
        }
        //根据 回复类型的枚举类CommentTypeEnum 判断 回复问题或回复评论
        if (comment.getType() == CommentTypeEnum.QUESTION.getType()){
            //回复问题
            //查询获取父类问题
            Question parentQuestion = questionDao.selectById(comment.getParentId());
            //异常处理：验证 回复的问题是否存在
            if (parentQuestion == null){
                throw new CustomizeException((CustomizeStatusCode.PARENT_QUESTION_NOT_FOUND));
            }

            commentDao.insert(comment);//新增评论

            //更新问题的回复数
            LambdaUpdateWrapper<Question> luwQuestion = new LambdaUpdateWrapper<>();
            luwQuestion.eq(Question::getId,parentQuestion.getId()).setSql("`comment_count` = `comment_count` + 1");
            questionDao.update(null,luwQuestion);

            //新增通知
            createNotification(comment, commentAuthor.getName(), parentQuestion.getCreator(), parentQuestion.getTitle(), NotificationTypeEnum.REPLY_QUESTION);
        }else {
            //回复评论
            //查询获取父类评论
            Comment parentComment = commentDao.selectById(comment.getParentId());
            //异常处理：验证 回复的评论是否存在
            if (parentComment == null){
                throw new CustomizeException(CustomizeStatusCode.PARNET_COMMENT_NOT_FOUND);
            }

            commentDao.insert(comment);//新增二级评论

            //更新父类评论的评论数
            LambdaUpdateWrapper<Comment> luwComment = new LambdaUpdateWrapper<>();
            luwComment.eq(Comment::getId,parentComment.getId()).setSql("`comment_count` = `comment_count` + 1");
            commentDao.update(null,luwComment);

            //新增通知
            createNotification(comment, commentAuthor.getName(), parentComment.getCommentAuthor(), parentComment.getContent(), NotificationTypeEnum.REPLY_COMMENT);
        }

        return new ResultDTO(CustomizeStatusCode.SUCCESS);
    }

    private void createNotification(Comment comment, String senderName, Integer receiver, String parentTitle, NotificationTypeEnum notificationTypeEnum) {
        Notification notification = new Notification();
        notification.setSender(comment.getCommentAuthor());
        notification.setSenderName(senderName);
        notification.setReceiver(receiver);
        notification.setParentId(comment.getParentId());
        notification.setParentTitle(parentTitle);
        notification.setType(notificationTypeEnum.getType());
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());

        notificationDao.insert(notification);
    }

    //回复列表功能(按时间倒序)
    @Override
    public List<CommentDTO> list(Integer id, CommentTypeEnum type) {
        //查询id和type对应的问题回复(type = 1) 或 二级评论(type = 2)
        LambdaQueryWrapper<Comment> lqwComment = new LambdaQueryWrapper<>();
        lqwComment.eq(Comment::getParentId,id)
                .eq(Comment::getType,type.getType())
                .orderByDesc(Comment::getGmtCreate);//按时间倒序
        List<Comment> commentList = commentDao.selectList(lqwComment);

        if (commentList.size() == 0){
            return new ArrayList<>();
        }

        //获取去重的所有评论人commentAuthor
        //使用Java 8 Stream提高效率
        //取出commentList的每一个comment的回复人id(commentAuthor)，经过distinct()去重，放进一个List中
        List<Integer> commentAuthors = commentList.stream().map(comment -> comment.getCommentAuthor()).distinct().collect(Collectors.toList());

        //获取所有评论人user信息并转换成Map
        //根据commentAuthors查询所有回复人的user信息
        LambdaQueryWrapper<User> lqwUser = new LambdaQueryWrapper<>();
        lqwUser.in(User::getAccountId,commentAuthors);
        List<User> userList = userDao.selectList(lqwUser);
        //取出userList中 每一个user的accountId作为key，对应的user作为value，创建出一个Map
        Map<Integer, User> userMap = userList.stream().collect(Collectors.toMap(user -> user.getAccountId(), user -> user));

        //获取所有CommentDTO，转换comment为CommentDTO
        //如果直接遍历commentList，里面再遍历userList(没有userMap的前提下)，把comment信息和对应的user信息放进每一个CommentDTO的话，时间复杂度是n²
        //使用Java 8 Stream提高效率
        //取出commentList中的每一个comment，映射成每一个CommentDTO对象，放进commentDTOList
        List<CommentDTO> commentDTOList = commentList.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);//快速复制comment信息
            commentDTO.setUser(userMap.get(comment.getCommentAuthor()));//根据评论人id在userMap中找出对应user信息，放进commentDTO
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOList;
    }
}
