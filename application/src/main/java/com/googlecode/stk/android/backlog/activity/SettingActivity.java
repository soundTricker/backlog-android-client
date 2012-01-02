package com.googlecode.stk.android.backlog.activity;

import roboguice.event.EventManager;
import roboguice.inject.InjectPreference;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.widget.ListView;

import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.service.BacklogService;

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

//	@Inject
//	protected Dao<IssueType , Integer> issueTypeDao;
//
//	@Inject
//	protected Dao<Component , Integer> componentDao;
//
//	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if(KeyEvent.KEYCODE_BACK == keyCode) {

			if(!backlogService.isSetuped()) {

				return super.onKeyUp(keyCode, event);
			}

//			dialog = new ProgressDialog(this);
//
//			dialog.setCancelable(false);
//
//			dialog.setTitle("セットアップ中");
//
//			dialog.setMessage("セットアップを行っています");
//
//			dialog.show();
//
//			backlogService.getProjectsAsync(new NonDialogAsyncTaskCallBack<Project[]>() {
//
//				@Override
//				public void onSuccess(Project[] result) {
//					eventManager.fire(SettingActivity.this,new ProjectsLoadFinishEvent(result));
//				}
//			});
//
//			return false;
			finish();
		}

		return super.onKeyUp(keyCode, event);
	}

//	public void handleProjectLoadFinishEvents(@Observes ProjectsLoadFinishEvent e) {
//		for (Project project : e.projects) {
//			processInsertIssueTypes(project);
//		}
//	}
//
//	public void handleInsertIssueTypesEvent(@Observes InsertedIssueTypeEvent e) {
//
//		processInsertComponents(e.project);
//
//		dialog.dismiss();
//		finish();
//	}
//
//	private void processInsertComponents(final Project project) {
//		backlogService.getComponentsAsync(project.id, new NonDialogAsyncTaskCallBack<Component[]>() {
//
//			@Override
//			public void onSuccess(Component[] result) {
//				componentDao.deleteAll();
//				componentDao.bulkInsert(result);
//				eventManager.fire(SettingActivity.this , new InsertedComponentEvent(project));
//			}
//		});
//	}
//
//	private void processInsertIssueTypes(final Project project) {
//		backlogService.getIssueTypesAsync(project.id, new NonDialogAsyncTaskCallBack<IssueType[]>() {
//
//			@Override
//			public void onSuccess(IssueType[] result) {
//				issueTypeDao.deleteAll();
//				issueTypeDao.bulkInsert(result);
//				eventManager.fire(SettingActivity.this, new InsertedIssueTypeEvent(project));
//			}
//		});
//	}
//
//	public static class ProjectsLoadFinishEvent {
//
//		public final Project[] projects;
//
//		public ProjectsLoadFinishEvent(Project[] result) {
//			this.projects = result;
//		}
//
//	}
//
//	public static class InsertedIssueTypeEvent {
//
//		public final Project project;
//
//		public InsertedIssueTypeEvent(Project result) {
//			this.project = result;
//		}
//	}
//
//	public static class InsertedComponentEvent {
//
//		public final Project project;
//
//		public InsertedComponentEvent(Project project) {
//			this.project = project;
//		}
//
//	}


}
