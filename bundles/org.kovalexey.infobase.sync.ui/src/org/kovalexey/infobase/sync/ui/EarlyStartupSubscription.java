package org.kovalexey.infobase.sync.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.google.inject.Inject;

public class EarlyStartupSubscription implements IStartup {

	@Inject
	IInfobaseServiceProvider service;

	@Override
	public void earlyStartup() {
		
		UIJob job = new UIJob("Инициализация подписок получения изменений") {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench.isStarting()) {
					schedule(1000);
				} else {
					var injector = Activator.getDefault().getInjector();
					service.addSynchronizationListener(injector.getInstance(InfobaseSyncListener.class));
				}
				return Status.OK_STATUS;
			}
		};
		
		job.schedule();
	}

}
