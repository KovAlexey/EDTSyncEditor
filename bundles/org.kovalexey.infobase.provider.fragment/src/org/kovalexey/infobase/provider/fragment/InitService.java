package org.kovalexey.infobase.provider.fragment;

import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com._1c.g5.v8.dt.internal.platform.services.core.SpyInjectProvider;
import com.google.inject.Injector;

import org.kovalexey.infobase.sync.ui.Activator;
import org.kovalexey.infobase.sync.ui.IInfobaseServiceProvider;

public class InitService implements IStartup {
	
	@Override
	public void earlyStartup() {
		Injector coreInjector = SpyInjectProvider.getCoreInjector();
		
		IInfobaseServiceProvider infobaseService = coreInjector.getInstance(InfobaseServiceProviderImpl.class);
		
		BundleContext context = FrameworkUtil.getBundle(Activator.class).getBundleContext();
		context.registerService(IInfobaseServiceProvider.class, infobaseService, null);
	}

}
