package com.googlecode.stk.android.backlog;

import java.util.ArrayList;
import java.util.List;

import android.test.ApplicationTestCase;

import com.google.inject.Module;
import com.googlecode.stk.android.backlog.inject.BacklogModule;

public class BacklogApplicationTest extends ApplicationTestCase<BacklogApplication> {

	public BacklogApplicationTest() {
		super(BacklogApplication.class);
	}

	public void test(){
		createApplication();
		BacklogApplication application = getApplication();

		List<Module> list = new ArrayList<Module>();
		application.addApplicationModules(list);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertTrue(list.get(0) instanceof BacklogModule);
	}

}
