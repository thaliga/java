package org.th.spring.customscope;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.th.spring.customscope.bean.ContextInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "classpath:/spring-test-th-customscope.xml" })
public class CustomScopeExecutorTest {

	@Autowired
	private ContextInfo contextInfo;

	@Test
	public void execute_EmptyRunnable() throws Exception {

		// when
		CustomScopeExecutor.execute(() -> {
		});
	}

	@Test
	public void execute_ContextInfoInContext() throws Exception {

		// when
		CustomScopeExecutor.execute(() -> {
			contextInfo.toString();
		});
	}

	@Test(expected = BeanCreationException.class)
	public void execute_ContextInfoWithoutContext() throws Exception {

		// when
		contextInfo.toString();
	}
}
