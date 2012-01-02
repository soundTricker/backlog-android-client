//
// DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED USING AndroidAnnotations.
//


package com.googlecode.stk.android.backlog.activity;

import android.R.id;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import com.google.inject.Injector;
import com.googlecode.androidannotations.api.SdkVersionHelper;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnContentChangedEvent;
import roboguice.activity.event.OnContentViewAvailableEvent;
import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnNewIntentEvent;
import roboguice.activity.event.OnPauseEvent;
import roboguice.activity.event.OnRestartEvent;
import roboguice.activity.event.OnResumeEvent;
import roboguice.activity.event.OnStartEvent;
import roboguice.activity.event.OnStopEvent;
import roboguice.event.EventManager;
import roboguice.inject.ContextScope;
import roboguice.inject.InjectorProvider;

public final class SettingActivity_
    extends SettingActivity
    implements InjectorProvider
{

    private ContextScope scope_;
    private EventManager eventManager_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        beforeCreate_(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    private void beforeCreate_(Bundle savedInstanceState) {
        Injector injector_ = getInjector();
        scope_ = injector_.getInstance(ContextScope.class);
        scope_.enter(this);
        injector_.injectMembers(this);
        eventManager_ = injector_.getInstance(EventManager.class);
        eventManager_.fire(new OnCreateEvent(savedInstanceState));
    }

    private void afterSetContentView_() {
        scope_.injectViews();
        eventManager_.fire(new OnContentViewAvailableEvent());
        listView = ((ListView) findViewById(id.list));
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        afterSetContentView_();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        afterSetContentView_();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        afterSetContentView_();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (((SdkVersionHelper.getSdkInt()< 5)&&(keyCode == KeyEvent.KEYCODE_BACK))&&(event.getRepeatCount() == 0)) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRestart() {
        scope_.enter(this);
        super.onRestart();
        eventManager_.fire(new OnRestartEvent());
    }

    @Override
    public void onStart() {
        scope_.enter(this);
        super.onStart();
        eventManager_.fire(new OnStartEvent());
    }

    @Override
    public void onResume() {
        scope_.enter(this);
        super.onResume();
        eventManager_.fire(new OnResumeEvent());
    }

    @Override
    public void onPause() {
        super.onPause();
        eventManager_.fire(new OnPauseEvent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        scope_.enter(this);
        eventManager_.fire(new OnNewIntentEvent());
    }

    @Override
    public void onStop() {
        scope_.enter(this);
        try {
            eventManager_.fire(new OnStopEvent());
        } finally {
            scope_.exit(this);
            super.onStop();
        }
    }

    @Override
    public void onDestroy() {
        scope_.enter(this);
        try {
            eventManager_.fire(new OnDestroyEvent());
        } finally {
            eventManager_.clear(this);
            scope_.exit(this);
            scope_.dispose(this);
            super.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager_.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        eventManager_.fire(new OnContentChangedEvent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        scope_.enter(this);
        try {
            eventManager_.fire(new OnActivityResultEvent(requestCode, resultCode, data));
        } finally {
            scope_.exit(this);
        }
    }

    @Override
    public Injector getInjector() {
        return ((InjectorProvider) getApplication()).getInjector();
    }

}
