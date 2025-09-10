package org.kovalexey.infobase.sync.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.IInfobaseSynchronizationListener;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.InfobaseEqualityState;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.InfobaseSynchronizationState;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com.google.inject.Inject;

public class InfobaseSyncListener implements IInfobaseSynchronizationListener {
	
	@Inject
	IInfobaseServiceProvider infobaseServiceProvider;
	@Inject
	InfobaseChangesCache infobaseChangesCache;
	@Inject
	IApplicationsServiceProvider applicationServiceProvider;

	@Override
	public void synchronizationStateChanged(InfobaseReference p0, InfobaseSynchronizationState p1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void equalityStateChanged(InfobaseReference infobase, InfobaseEqualityState p1) {
		System.out.println("equalityStateChanged");
		var application = infobaseServiceProvider.getApplicationFromInfobase(infobase);
		if (application == null) {
			return;
		}
		
		Job job = new Job("Получение изменений") {
			
			@Override
			public IStatus run(IProgressMonitor monitor) {
				var changes = infobaseServiceProvider.getChanges(application);
				infobaseChangesCache.clearRecords(application);
				for (String name : changes) {
					infobaseChangesCache.addRecord(application, name);
				}
				
				System.out.println(changes);
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						var page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						var view = page.findView("com.e1c.g5.dt.applications.ui.view");
						if (view == null) {
							return;
						}
						
						applicationServiceProvider.updateIfApplicationView(view);
						System.out.println(view);
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		
		
	}
	

}
