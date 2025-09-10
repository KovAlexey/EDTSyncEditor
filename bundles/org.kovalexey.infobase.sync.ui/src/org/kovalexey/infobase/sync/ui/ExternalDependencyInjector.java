package org.kovalexey.infobase.sync.ui;

import org.eclipse.core.runtime.Plugin;

import com._1c.g5.wiring.AbstractServiceAwareModule;

public class ExternalDependencyInjector extends AbstractServiceAwareModule {
	
	public ExternalDependencyInjector(Plugin context) {
		super(context);
	}

	@Override
	protected void doConfigure() {
		bind(IInfobaseServiceProvider.class).toService();
		bind(IApplicationsServiceProvider.class).toService();
	}

}
