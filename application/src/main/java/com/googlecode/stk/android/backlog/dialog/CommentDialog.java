package com.googlecode.stk.android.backlog.dialog;

import roboguice.event.EventManager;
import roboguice.inject.InjectorProvider;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.googlecode.stk.android.backlog.db.entity.Comment;
import com.googlecode.stk.android.backlog.service.BacklogService;


public class CommentDialog implements OnClickListener {

	@Inject
	private BacklogService backlogService;
	
	@Inject
	private EventManager eventManager;
	
	private EditText editText;

	private final String issueKey;
	

	private final Context context;
	
	private CommentDialog(Context context,String issueKey) {
		this.context = context;
		this.issueKey = issueKey;
	}
	
	public static AlertDialog createDialog(String issueKey,Context context) {
		
		CommentDialog impl = new CommentDialog(context,issueKey);
		
		InjectorProvider injectProvier = (InjectorProvider) context;
		
		injectProvier.getInjector().injectMembers(impl);
		
		impl.editText = new EditText(context);
		impl.editText.setLines(3);
		
		AlertDialog dialog = new AlertDialog.Builder(context)
								.setTitle("Commentを追加")
								.setView(impl.editText)
								.setPositiveButton("OK", impl)
								.setNegativeButton("Cancel", null)
								.create();
		
		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		if(Strings.isNullOrEmpty(editText.getText().toString())) {
			Toast.makeText(context, "コメントが入力されていません。", Toast.LENGTH_SHORT).show();
			return;
		}
		
		new RoboAsyncTask<Comment>(){

			ProgressDialog progressDialog;
			
			@Override
			protected void onPreExecute() throws Exception {
				
				super.onPreExecute();
				
				progressDialog = new ProgressDialog(context);
				
				progressDialog.setMessage("コメントを追加中");
				
				progressDialog.show();
			}
			
			@Override
			public Comment call() throws Exception {
				return backlogService.addComment(issueKey, editText.getText().toString());
			}
			
			@Override
			protected void onSuccess(Comment t) throws Exception {
				progressDialog.dismiss();
				Toast.makeText(context, "コメントを追加しました。", Toast.LENGTH_LONG).show();
				eventManager.fire(context, new OnAddCommentSuccessEvent(t));
			}
			
			@Override
			protected void onException(Exception e) throws RuntimeException {
				Ln.e(e,e.getMessage());
				progressDialog.dismiss();
				
				Toast.makeText(context, "コメントの追加に失敗しました。", Toast.LENGTH_LONG).show();
				
			}
			
		}.execute();
		
	}
	
	public static class OnAddCommentSuccessEvent {
		
		public final Comment comment;

		public OnAddCommentSuccessEvent(Comment comment) {
			this.comment = comment;
		}
		
	}
}
