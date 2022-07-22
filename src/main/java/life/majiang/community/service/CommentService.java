package life.majiang.community.service;

import life.majiang.community.dto.CommentCreateDTO;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.enums.CommentTypeEnum;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author CZS
 * @create 2022-07-14 12:30
 *
 * 业务层接口：回复功能
 *
 **/
public interface CommentService {

    //回复功能
    Object postComment(CommentCreateDTO commentCreateDTO, HttpServletRequest request);

    //回复列表功能(按时间倒序)
    List<CommentDTO> list(Integer id, CommentTypeEnum type);
}
