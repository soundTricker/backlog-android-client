package com.googlecode.stk.android.backlog.activity;

import java.sql.SQLException;
import java.util.List;

import org.xmlrpc.android.XMLRPCException;

import roboguice.event.EventManager;
import roboguice.inject.InjectPreference;
import roboguice.util.Ln;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.widget.ListView;

import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.db.entity.Component;
import com.googlecode.stk.android.backlog.db.entity.IssueType;
import com.googlecode.stk.android.backlog.db.entity.Priority;
import com.googlecode.stk.android.backlog.db.entity.Project;
import com.googlecode.stk.android.backlog.db.entity.Resolution;
import com.googlecode.stk.android.backlog.db.entity.Status;
import com.googlecode.stk.android.backlog.db.entity.User;
import com.googlecode.stk.android.backlog.db.entity.Version;
import com.googlecode.stk.android.backlog.service.BacklogService;
import com.j256.ormlite.dao.Dao;

@EActivity
@RoboGuice
public class SettingActivity extends PreferenceActivity {

	@InjectPreference("spaceId")
	protected EditTextPreference spaceIdEditText;

	@ViewById(android.R.id.list)
	protected ListView listView;

	@Inject
	protected EventManager eventManager;

	@Inject
	BacklogService backlogService;

	@Inject
	protected Dao<IssueType , Integer> issueTypeDao;

	@Inject
	protected Dao<Component , Integer> componentDao;
	
	@Inject
	protected Dao<Version , Integer> versionDao;
	
	@Inject
	protected Dao<Priority , Integer> priorityDao;
	
	@Inject
	protected Dao<Status , Integer> statusDao;
	
	@Inject
	protected Dao<Project , Integer> projectDao;
	
	@Inject
	protected Dao<Resolution, Integer> resolutionDao;
	
	@Inject
	protected Dao<User, Integer> userDao;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if(KeyEvent.KEYCODE_BACK == keyCode) {
			
			backlogService.reload();
			
			if(!backlogService.isSetuped()) {

				return super.onKeyUp(keyCode, event);
			}

			dialog = new ProgressDialog(this);

			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			dialog.setCancelable(false);

			dialog.setTitle("セットアップ中");
			
			dialog.setMessage("セットアップを行っています");
			
			dialog.show();

			loadProject();
			
			return false;
		}

		return super.onKeyUp(keyCode, event);
	}

	@Background
	public void loadProject() {
		try {

			List<Status> statuses = backlogService.getStatuses();
			
			for (Status status : statuses) {
				statusDao.createOrUpdate(status);
				Ln.d("put status id:%d" , status.id); 
			}
			
			List<Priority> priorities = backlogService.getPriorities();
			
			for (Priority priority : priorities) {
				priorityDao.createOrUpdate(priority);
				Ln.d("put priority id:%d" , priority.id); 
			}
			
			List<Resolution> resolutions = backlogService.getResolutions();
			
			for (Resolution resolution : resolutions) {
				resolutionDao.createOrUpdate(resolution);
				Ln.d("put resolution id:%d" , resolution.id); 
			}
			
			List<Project> projects = backlogService.getProjects();
			
			dialog.setMax(projects.size());

			for (Project project : projects) {
				
				projectDao.createOrUpdate(project);
				Ln.d("put project id:%d" , project.id); 
				
				List<IssueType> issueTypes = backlogService.getIssueTypes(project.id);
				
				for (IssueType issueType : issueTypes) {
					
					issueTypeDao.createOrUpdate(issueType);
					Ln.d("put issueType id:%d name:%s color:%s" , issueType.id, issueType.name, issueType.color); 
				}
				
				List<Component> components = backlogService.getComponents(project.id);
				
				for (Component component : components) {
					
					componentDao.createOrUpdate(component);
					Ln.d("put component id:%d" , component.id); 
				}
				
				List<Version> versions = backlogService.getVersions(project.id);
				
				for (Version version : versions) {
					versionDao.createOrUpdate(version);
					Ln.d("put version id:%d" , version.id); 
				}
				
				List<User> users = backlogService.getUsers(project.id);
				
				for (User user : users) {
					
					userDao.createOrUpdate(user);
					
					Ln.d("put user id:%d" , user.id);
				}
				
				dialog.setProgress(dialog.getProgress() + 1);
			}
			Ln.d("finish setup"); 

			finishSetup();
		} catch (XMLRPCException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@UiThread
	public void finishSetup() {
		Ln.d("finish setup");
		if(dialog != null) {
			dialog.setProgress(dialog.getMax());
			
			dialog.setMessage("完了しました");
			dialog.dismiss();
		}
		
		finish();
	}
}
