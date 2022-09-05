package life.majiang.community.service;

import life.majiang.community.domain.Comment;
import life.majiang.community.domain.User;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.enums.CommentTypeEnum;

import java.util.List;

/**
 * @author CZS
 * @create 2022-07-14 12:30
 * <p>
 * 业务层接口：回复功能
 **/
public interface CommentService {

    //创建回复
    void postComment(Comment comment, User commentAuthor);

    //回复列表功能(按时间倒序)
    List<CommentDTO> list(Integer id, CommentTypeEnum type);
}
