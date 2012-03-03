package com.googlecode.stk.android.backlog.dialog;

import java.sql.SQLException;
import java.util.List;

import roboguice.adapter.IterableAdapter;
import roboguice.event.EventManager;
import roboguice.inject.InjectorProvider;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.googlecode.stk.android.backlog.R;
import com.googlecode.stk.android.backlog.db.entity.Issue;
import com.googlecode.stk.android.backlog.db.entity.Project;
import com.googlecode.stk.android.backlog.db.entity.ProjectUserRelation;
import com.googlecode.stk.android.backlog.db.entity.Resolution;
import com.googlecode.stk.android.backlog.db.entity.Status;
import com.googlecode.stk.android.backlog.service.BacklogService;
import com.j256.ormlite.dao.Dao;

public class SwitchStatusDialog implements OnClickListener {

	private final Context context;

	private final Issue issue;

	private View view;

	@Inject
	BacklogService backlogService;

	@Inject
	Dao<Resolution, Integer> resolutionDao;

	@Inject
	Dao<Status, Integer> statusDao;

	@Inject
	Dao<ProjectUserRelation, Integer> userRelDao;

	@Inject
	Dao<Project, Integer> projectDao;

	@Inject
	LayoutInflater inflater;

	@Inject
	Dao<ProjectUserRelation, Integer> relDao;

	@Inject
	private EventManager eventManager;

	private Spinner status;

	private Spinner assigner;

	private Spinner resolution;

	private LinearLayout resolutionPanel;

	private EditText comment;

	private SwitchStatusDialog(Context context,
		Issue issue) {
		this.context = context;
		this.issue = issue;
		InjectorProvider injectProvier = (InjectorProvider) context;
		injectProvier.getInjector().injectMembers(this);
		View view = inflater.inflate(R.layout.switch_status, null);
		this.view = view;
		this.status = (Spinner) view.findViewById(R.id.status);
		this.assigner = (Spinner) view.findViewById(R.id.assigner);
		this.resolution = (Spinner) view.findViewById(R.id.resolution);
		this.resolutionPanel = (LinearLayout) view.findViewById(R.id.resolutionPanel);
		this.comment = (EditText) view.findViewById(R.id.comment);
	}

	public static AlertDialog createDialog(Context context, Issue issue) {
		SwitchStatusDialog impl = new SwitchStatusDialog(context, issue);

		impl.bind();

		AlertDialog dialog = new AlertDialog.Builder(context).setTitle("Commentを追加").setView(impl.view).setPositiveButton("OK", impl).setNegativeButton("Cancel", null).create();

		return dialog;
	}

	private void bind() {

		bindViews();
	}

	private void bindViews() {
		try {

			bindAssigner();
			
			bindStatus();

			bindResolution();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void bindResolution() throws SQLException {
		List<Resolution> resolutionList = resolutionDao.queryForAll();

		IterableAdapter<Resolution> resolutionAdapter = new IterableAdapter<Resolution>(context, 0, resolutionList) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				if (convertView == null) {
					convertView = new TextView(context);
				}

				((TextView) convertView).setText(getItem(position).name);

				return convertView;
			}
		};

		resolution.setAdapter(resolutionAdapter);
	}

	private void bindStatus() throws SQLException {
		List<Status> statusList = statusDao.queryBuilder().where().ne("_ID", issue.id).query();

		final IterableAdapter<Status> statusAdapter = new IterableAdapter<Status>(context, 0, statusList) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				if (convertView == null) {
					convertView = new TextView(context);
				}

				((TextView) convertView).setText(getItem(position).name);

				return convertView;
			}
		};

		status.setAdapter(statusAdapter);
		
		for (int i = 0; i < statusList.size(); i++) {
			
			Status s = statusList.get(i);
			
			if(issue.status != null 
				&& issue.status.id != null
				&& issue.status.id.equals(s.id)) {
				status.setSelection(i);
				break;
			}
			
		}

		status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Status item = statusAdapter.getItem(position);

				if (item.id.equals(Status.FINISH_ID)) {
					resolutionPanel.setVisibility(View.VISIBLE);
					resolutionPanel.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				} else {
					resolutionPanel.setVisibility(View.INVISIBLE);
					resolutionPanel.setLayoutParams(new LayoutParams(0, 0));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void bindAssigner() throws SQLException {
		String projectKey = issue.key.split("-")[0];

		List<Project> list = projectDao.queryBuilder().where().eq("key", projectKey).query();

		List<ProjectUserRelation> projectUserRelList = userRelDao.queryBuilder().where().eq("project_id", list.get(0).id).query();

		final IterableAdapter<ProjectUserRelation> assignerAdapter = new IterableAdapter<ProjectUserRelation>(context, 0, projectUserRelList) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = new TextView(context);
				}

				((TextView) convertView).setText(getItem(position).user.name);

				return convertView;
			}
		};

		assigner.setAdapter(assignerAdapter);
		
		for (int i = 0; i < projectUserRelList.size(); i++) {
			ProjectUserRelation projectUserRelation = projectUserRelList.get(i);
			
			if(issue.assigner != null 
				&& issue.assigner.id != null 
				&& issue.assigner.id.equals(projectUserRelation.user.id)) {
				assigner.setSelection(i);
				break;
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int witch) {
		
		new RoboAsyncTask<Issue>() {
			
			ProgressDialog progressDialog;
			
			@Override
			protected void onPreExecute() throws Exception {
				
				super.onPreExecute();
				
				progressDialog = new ProgressDialog(context);
				
				progressDialog.setMessage("ステータスを変更中");
				
				progressDialog.setCancelable(false);
				
				progressDialog.show();
			}
			

			@Override
			public Issue call() throws Exception {
				Status selectedStatus = (Status) status.getSelectedItem();
				
				Resolution selectedResolution = null;
				
				if(Status.FINISH_ID == selectedStatus.id) {
					selectedResolution = (Resolution)resolution.getSelectedItem();
				}
				
				ProjectUserRelation selectedAssigner = (ProjectUserRelation)assigner.getSelectedItem();
				
				String commentText = Strings.emptyToNull(comment.getText().toString());

				return backlogService.switchStatus(issue.key,
					selectedStatus,
					selectedResolution,
					selectedAssigner,
					commentText);
			}
			
			@Override
			protected void onSuccess(Issue t) throws Exception {
				progressDialog.dismiss();
				Toast.makeText(context, "ステータスを変更しました。", Toast.LENGTH_LONG).show();
				eventManager.fire(context, new OnSwitchStatusSuccessEvent(t));
			}
			
			@Override
			protected void onException(Exception e) throws RuntimeException {
				Ln.e(e,e.getMessage());
				progressDialog.dismiss();
				
				Toast.makeText(context, "ステータスの変更に失敗しました。", Toast.LENGTH_LONG).show();
			}
		}.execute();
		
	}
	
	public static class OnSwitchStatusSuccessEvent {
		
		public final Issue updatedIssue;

		public OnSwitchStatusSuccessEvent(Issue updatedIssue) {
			this.updatedIssue = updatedIssue;
			
		}
	}

}
