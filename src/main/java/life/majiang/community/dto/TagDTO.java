package life.majiang.community.dto;

import lombok.Data;

import java.util.List;

/**
 * @author CZS
 * @create 2022-07-23 22:40
 **/
@Data
public class TagDTO {
    private String categoryName;//标签库分组标签的组名
    private List<String> tags;//每一组标签的具体标签
}
