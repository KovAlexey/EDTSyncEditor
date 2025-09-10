package org.kovalexey.infobase.sync.ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.platform.services.core.infobases.sync.IInfobaseSynchronizationListener;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com.e1c.g5.dt.applications.infobases.IInfobaseApplication;
import com.google.inject.Injector;

public interface IInfobaseServiceProvider {
	public Boolean isSynchronized(IProject project, InfobaseReference infobase);
	public Boolean isEqual(IProject project, InfobaseReference infobase);
	public Boolean isLoading(IProject project, InfobaseReference infobase);
	public void setInfobaseSynchronized(IProject project, InfobaseReference infobase);
	public void setInfobaseSynchronized(IProject project, InfobaseReference infobase, IProgressMonitor monitor);
	public IProject getProjectFromInfobaseApplication(IInfobaseApplication application);
	public Injector getCoreInjector();
	public void addSynchronizationListener(IInfobaseSynchronizationListener listener);
	public ArrayList<String> getChanges(IInfobaseApplication application);
	IInfobaseApplication getApplicationFromInfobase(InfobaseReference infobase);
}
