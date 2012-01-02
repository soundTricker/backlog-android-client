package com.googlecode.stk.android.backlog;

import roboguice.test.RoboActivityUnitTestCase;
import android.content.Intent;
import android.test.suitebuilder.annotation.MediumTest;

import com.googlecode.stk.android.backlog.activity.TopActivity;

public class TopActivityTest extends RoboActivityUnitTestCase<TopActivity> {

	public TopActivityTest() {
		super(TopActivity.class);
	}

	@MediumTest
	public void testActivity() {
		BacklogApplication application = new BacklogApplication(getInstrumentation().getTargetContext());

		setApplication(application);

		Intent intent = new Intent(Intent.ACTION_MAIN);

		TopActivity activity = startActivity(intent, null, null);
		assertNotNull(activity);
	}
}
