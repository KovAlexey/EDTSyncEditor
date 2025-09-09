package org.kovalexey.infobase.sync.ui.changedrecords;

import com.e1c.g5.dt.applications.IApplication;
import com.e1c.g5.dt.applications.ILifecycleAware;
import org.eclipse.core.runtime.PlatformObject;

public class ChangeRecord extends PlatformObject implements ILifecycleAware {
	
	private final String name;
	private final IApplication app;
	
	public ChangeRecord(String name, IApplication app) {
		super();
		this.name = name;
		this.app = app;
	}

	public String getName() {
		return name;
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
