package life.majiang.community;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
//		String str = ",javascript,html5";
////		String[] split = str.split("，|,");
//		str = StringUtils.replace(str,"，",",");
//		System.out.println(str);
//		String[] split = StringUtils.split(str, ",");
//		for (int i = 0; i < split.length; i++) {
//			System.out.println(split[i]);
//		}
//		//WHERE (id <> ? AND tag regexp '|javascript|html5')
//
//		System.out.println();
//
//		List<String> list = Arrays.asList(split);
//		System.out.println(list);
//
//		String regexp = list.stream().collect(Collectors.joining("|"));
//		System.out.println(regexp);
//
//		System.out.println();
//
//		LambdaQueryWrapper<Question> lqw = new LambdaQueryWrapper<>();
//		lqw.apply("tag regexp '" + regexp + "'");
//		List<Question> relatedQuestions = questionDao.selectList(lqw);
//
//		Integer count = questionDao.selectCount(lqw);
//
//		System.out.println(relatedQuestions);
//		System.out.println(count);
//
//		System.out.println("************************");
//
//		Integer countBySearch = questionDao.countBySearch("更新|tag2");
//		System.out.println(countBySearch);
//
//		System.out.println("************************");

		IPage<Question> page = new Page<>(3,5);
		LambdaQueryWrapper<Question> lqwQuestion = new LambdaQueryWrapper<>();
		lqwQuestion.apply("title regexp '更新|测试'");
		lqwQuestion.orderByDesc(Question::getGmtCreate);//按时间倒序
		questionDao.selectPage(page,lqwQuestion);

	}

}
