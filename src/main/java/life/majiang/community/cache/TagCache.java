package life.majiang.community.cache;

import life.majiang.community.dto.TagDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CZS
 * @create 2022-07-23 22:35
 * <p>
 * 标签缓存，在后端缓存好发送到前端展示
 **/
public class TagCache {
    public static List<TagDTO> get() {
        List<TagDTO> tagDTOList = new ArrayList<>();
        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("javascript", "php", "css", "html", "html5", "java", "node.js", "python", "c++", "c", "golang", "objective-c", "typescript", "shell", "swift", "c#", "sass", "ruby", "bash", "less", "asp.net", "lua", "scala", "coffeescript", "actionscript", "rust", "erlang", "perl"));
        tagDTOList.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("laravel", "spring", "express", "django", "flask", "yii", "ruby-on-rails", "tornado", "koa", "struts"));
        tagDTOList.add(framework);


        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("linux", "nginx", "docker", "apache", "ubuntu", "centos", "缓存 tomcat", "负载均衡", "unix", "hadoop", "windows-server"));
        tagDTOList.add(server);

        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql", "redis", "mongodb", "sql", "oracle", "nosql memcached", "sqlserver", "postgresql", "sqlite"));
        tagDTOList.add(db);

        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTags(Arrays.asList("git", "github", "visual-studio-code", "vim", "sublime-text", "xcode intellij-idea", "eclipse", "maven", "ide", "svn", "visual-studio", "atom emacs", "textmate", "hg"));
        tagDTOList.add(tool);

        return tagDTOList;
    }

    public static String filterInvalid(String tags) {
        String[] split = tags.split(",|，");
        List<TagDTO> tagDTOList = get();

        //这里使用flatMap方法的分析：
        // 对于tagDTOList，里面有很多个TagDTO，每个TagDTO里有一个标签List装着很多个标签，相当于 集合(tagDTOList)套着集合(Tags) ---> 二维
        // 如果想把所有标签拿到，也就是把里面每一个TagDTO的Tags集合里的一个个标签拿出来，全部放在一起，用map方法会比较麻烦

        //对于这种 获取"二维"里的数据的情况，使用flatMap方法
        //flatMap(Function f)
        // ---> 将 流(tagDTOList的流) 中的 经过映射后的每一个值(tag.getTags()) 转换成 每一个对应的流(tag.getTags().stream()，这个流包含着对应Tags中的所有标签)，然后把 所有的流 连接成 一个流(所有Tags流中的标签都放在一个流)
        //  ---> 相当于把"二维"里的数据拍平，直接深入到"二维"中拿一个个数据放在一起
        List<String> tagList = tagDTOList.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());

        String invalid = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));

        return invalid;
    }
}
