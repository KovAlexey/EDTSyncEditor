package org.kovalexey.infobase.applications.ui.fragment;

import org.eclipse.ui.IStartup;
import org.kovalexey.infobase.sync.ui.Activator;
import org.kovalexey.infobase.sync.ui.IApplicationsServiceProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class InitService implements IStartup {

	@Override
	public void earlyStartup() {
		IApplicationsServiceProvider service = new AppicationsServiceProvider(); 
		
		BundleContext context = FrameworkUtil.getBundle(Activator.class).getBundleContext();
		context.registerService(IApplicationsServiceProvider.class, service, null);
	}

}
