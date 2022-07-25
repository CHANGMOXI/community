package life.majiang.community;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import life.majiang.community.dao.QuestionDao;
import life.majiang.community.domain.Question;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class CommunityApplicationTests {
	@Autowired
	private QuestionDao questionDao;

	@Test
	void contextLoads() {
		String str = ",javascript,html5";
//		String[] split = str.split("，|,");
		str = StringUtils.replace(str,"，",",");
		System.out.println(str);
		String[] split = StringUtils.split(str, ",");
		for (int i = 0; i < split.length; i++) {
			System.out.println(split[i]);
		}
		//WHERE (id <> ? AND tag regexp '|javascript|html5')

		System.out.println();

		List<String> list = Arrays.asList(split);
		System.out.println(list);

		String regexp = list.stream().collect(Collectors.joining("|"));
		System.out.println(regexp);

		System.out.println();

		LambdaQueryWrapper<Question> lqw = new LambdaQueryWrapper<>();
		lqw.ne(Question::getId,39).apply("tag regexp '" + regexp + "'");
		List<Question> relatedQuestions = questionDao.selectList(lqw);
		System.out.println(relatedQuestions);
	}

}
