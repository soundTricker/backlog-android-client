package com.googlecode.stk.android.backlog.service;

import java.util.List;

import org.xmlrpc.android.XMLRPCException;

import com.google.inject.ImplementedBy;
import com.googlecode.stk.android.backlog.db.entity.Component;
import com.googlecode.stk.android.backlog.db.entity.Issue;
import com.googlecode.stk.android.backlog.db.entity.IssueType;
import com.googlecode.stk.android.backlog.db.entity.Priority;
import com.googlecode.stk.android.backlog.db.entity.Project;
import com.googlecode.stk.android.backlog.db.entity.Resolution;
import com.googlecode.stk.android.backlog.db.entity.Status;
import com.googlecode.stk.android.backlog.db.entity.Timeline;
import com.googlecode.stk.android.backlog.db.entity.User;
import com.googlecode.stk.android.backlog.db.entity.UserIcon;
import com.googlecode.stk.android.backlog.db.entity.Version;
import com.googlecode.stk.android.backlog.service.impl.BacklogServiceImpl;

@ImplementedBy(BacklogServiceImpl.class)
public interface BacklogService {

	void setBasicAuthentication(String account, String password);

	List<Project> getProjects() throws XMLRPCException;

	List<Component> getComponents(Integer projectId) throws XMLRPCException;

	List<Version> getVersions(Integer projectId) throws XMLRPCException;

	List<User> getUsers(Integer projectId) throws XMLRPCException;

	void reload();

	boolean isSetuped();

	<T> T call(String methodName, Object ... params) throws XMLRPCException;

	List<IssueType> getIssueTypes(Integer id) throws XMLRPCException;

	List<Timeline> getTimeline() throws XMLRPCException;

	UserIcon getUserIcon(Integer id) throws XMLRPCException;

	Issue getIssue(int issueId) throws XMLRPCException;

	List<Status> getStatuses() throws XMLRPCException;

	List<Priority> getPriorities() throws XMLRPCException;

	List<Resolution> getResolutions() throws XMLRPCException;
}
