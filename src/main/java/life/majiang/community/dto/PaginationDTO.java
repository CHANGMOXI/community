package life.majiang.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CZS
 * @create 2022-07-15 20:57
 *
 * 分页DTO：一个PaginationDTO对象包括 当前页所有问题记录、当前页码、总页数、当前页码以及它前后可供选择的页码、首页、上一页、末尾页、下一页
 *
 **/

@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;//当前页 所有问题记录(所有QuestionDTO)
    private Integer currentPage;//当前页码
    private Integer totalPage;//总页数
    private List<Integer> pages = new ArrayList<>();//当前页码以及它前后可供选择的页码

    //本项目弱化前端，所以以下逻辑放在后端进行处理
    private boolean showFirstPage;//是否展示 首页
    private boolean showPrevious;//是否展示 上一页
    private boolean showEndPage;//是否展示 末尾页
    private boolean showNext;//是否展示 下一页

    //根据传参设置各个属性
    //形参halfRange 表示 当前页码 前后最多可展示的页码
    public void setPagination(List<QuestionDTO> questionDTOList, Long totalPages, Integer currentPage, Integer halfRange){
        //Long类型总页数转成int型方便后续操作
        //Long类型转int类型，Math.toIntExact方法在整数溢出时会抛异常，Long包装类的intValue方法不会抛异常并依然提供整数(由于溢出，一般不正确)
        int total = Math.toIntExact(totalPages);//

        //防止页面url的页码超出范围时，页码展示错误 ---> 放在分页查询之前处理了，所以传递进来不会超出范围

        this.questions = questionDTOList;//设置当前页 所有问题记录(所有QuestionDTO)
        this.currentPage = currentPage;//设置当前页码
        this.totalPage = total;//设置总页数

        //设置可供选择的页码
        for (int i = currentPage - halfRange; i <= currentPage + halfRange; i++) {
            if (i >= 1 && i <= total){
                this.pages.add(i);
            }
        }

        //是否展示 上一页
        if (currentPage == 1){
            showPrevious = false;
        }else {
            showPrevious = true;
        }
        //是否展示 下一页
        if (currentPage == total){
            showNext = false;
        }else {
            showNext = true;
        }

        //是否展示 首页
        if (this.pages.contains(1)){
            showFirstPage = false;
        }else {
            showFirstPage = true;
        }
        //是否展示 末尾页
        if (this.pages.contains(total)){
            showEndPage = false;
        }else {
            showEndPage = true;
        }
    }

}
