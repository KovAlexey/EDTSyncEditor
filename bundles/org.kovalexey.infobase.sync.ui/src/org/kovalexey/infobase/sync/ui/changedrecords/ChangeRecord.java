package org.kovalexey.infobase.sync.ui.changedrecords;

import com.e1c.g5.dt.applications.IApplication;
import com.e1c.g5.dt.applications.ILifecycleAware;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.PlatformObject;

public class ChangeRecord extends PlatformObject implements ILifecycleAware {
	
	private final String name;
	private final IProject project;
	private final IApplication app;
	
	public ChangeRecord(String name, IApplication app, IProject project) {
		super();
		this.name = name;
		this.project = project;
		this.app = app;
	}

	public String getName() {
		return String.format("(%s) - %s", project.getName(), name);
	}

	@Override
	public IApplication getApplication() {
		// TODO Auto-generated method stub
		return app;
	}
	
    public <T> T getAdapter(Class<T> adapter) {
        return (T)(adapter.isAssignableFrom(IApplication.class) ? adapter.cast(this.app) : super.getAdapter(adapter));
    }
	
}
