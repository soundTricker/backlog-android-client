package com.googlecode.stk.android.backlog.activity;

import com.googlecode.stk.android.backlog.db.entity.*;
import com.googlecode.stk.android.backlog.service.BacklogService;
import com.seventheye.robolectric.sqlite.util.SQLiteMap;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.util.DatabaseConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlrpc.android.XMLRPCException;
import roboguice.application.RoboApplication;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: ohashi keisuke
 * Date: 12/03/01
 * Time: 22:39
 * To change this template use File | Settings | File Templates.
 */
@RunWith(RobolectricTestRunner.class)
@DatabaseConfig.UsingDatabaseMap(SQLiteMap.class)
public class SettingActivityTest {

	@Test
	public void testLoadProject() throws Exception {
		Robolectric.application.onCreate();

		SettingActivity activity = new SettingActivity();

		assertThat(activity, is(notNullValue()));

		((RoboApplication)Robolectric.application).getInjector().injectMembers(activity);

		assertThat(activity.priorityDao , is(notNullValue()));

		BacklogService backlogService = createMockBacklogService();
		
		activity.backlogService = backlogService;
		
		activity.loadProject();
		
		assertThat(activity.statusDao.queryForId(-1) , is(notNullValue()));
		assertThat(activity.priorityDao.queryForId(-2) , is(notNullValue()));
		assertThat(activity.resolutionDao.queryForId(-3) , is(notNullValue()));
		assertThat(activity.projectDao.queryForId(-4) , is(notNullValue()));
		assertThat(activity.issueTypeDao.queryForId(-5) , is(notNullValue()));
		assertThat(activity.componentDao.queryForId(-6) , is(notNullValue()));
		assertThat(activity.versionDao.queryForId(-7) , is(notNullValue()));
		assertThat(activity.userDao.queryForId(-8) , is(notNullValue()));
		assertThat(activity.projectUserRelationDao.queryBuilder().where().eq("user_id" , -8).and().eq("project_id" , -4).query().size() , is(1));
		List<ProjectUserRelation> relationList = activity.projectUserRelationDao.queryBuilder().where().eq("user_id" , -8).and().eq("project_id" , -4).query();
	}

	public static BacklogService createMockBacklogService() throws XMLRPCException {
		BacklogService backlogService = mock(BacklogService.class);

		Status s = new Status();
		s.id = -1;
		s.name = "status";
		when(backlogService.getStatuses()).thenReturn(Arrays.asList(s));

		Priority p = new Priority();
		p.id = -2;
		p.name = "priority";
		when(backlogService.getPriorities()).thenReturn(Arrays.asList(p));

		Resolution r = new Resolution();
		r.id = -3;
		r.name = "resolution";
		when(backlogService.getResolutions()).thenReturn(Arrays.asList(r));

		Project project = new Project();
		project.id = -4;
		project.archived = false;
		project.key = "TST";
		project.name = "test project";
		project.url = "https://test-project.backlog.jp";

		when(backlogService.getProjects()).thenReturn(Arrays.asList(project));

		IssueType it = new IssueType();
		it.id = -5;
		it.color= "#FFFFFF";
		it.name = "issueType";
		it.projectId = -4;

		when(backlogService.getIssueTypes(-4)).thenReturn(Arrays.asList(it));

		Component c = new Component();
		c.id = -6;
		c.name = "component";
		c.projectId = -4;
		when(backlogService.getComponents(-4)).thenReturn(Arrays.asList(c));

		Version v = new Version();
		v.id = -7;
		v.date = new Date();
		v.name = "version";
		v.projectId = -4;
		when(backlogService.getVersions(-4)).thenReturn(Arrays.asList(v));

		User u = new User();
		u.id = -8;
		u.name = "user";
		when(backlogService.getUsers(-4)).thenReturn(Arrays.asList(u));
		return backlogService;
	}
}
