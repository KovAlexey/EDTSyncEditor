package org.kovalexey.infobase.sync.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;

import com._1c.g5.v8.dt.platform.services.core.infobases.sync.IInfobaseSynchronizationListener;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.InfobaseEqualityState;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.InfobaseSynchronizationState;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com.e1c.g5.dt.applications.IApplicationManager;
import com.e1c.g5.dt.applications.infobases.IInfobaseApplication;
import com.google.inject.Inject;

public class InfobaseSyncListener implements IInfobaseSynchronizationListener {
	
	@Inject
	IInfobaseServiceProvider infobaseServiceProvider;
	@Inject
	InfobaseChangesCache infobaseChangesCache;

	@Override
	public void synchronizationStateChanged(InfobaseReference p0, InfobaseSynchronizationState p1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void equalityStateChanged(InfobaseReference infobase, InfobaseEqualityState p1) {
		var application = infobaseServiceProvider.getApplicationFromInfobase(infobase);
		if (application == null) {
			return;
		}
		
//		UpdateJob job = new UpdateJob("Test", infobaseServiceProvider, infobaseChangesCache, application);
//		job.schedule();
		
//		Job job = new Job("Получение изменений") {
//			
//			@Override
//			protected IStatus run(IProgressMonitor monitor) {
//				var changes = infobaseServiceProvider.getChanges(application);
//				infobaseChangesCache.clearRecords(application);
//				return null;
//			}
//		};
		
		UIJob job = new UIJob("Получение изменений") {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				var changes = infobaseServiceProvider.getChanges(application);
				infobaseChangesCache.clearRecords(application);
				for (String name : changes) {
					infobaseChangesCache.addRecord(application, name);
				}
				
				System.out.println(changes);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		
		
	}
	
	private class UpdateJob extends Job {
		
		private IInfobaseServiceProvider infobaseServiceProvider;
		private InfobaseChangesCache cache;
		IInfobaseApplication application;

		
		
		public UpdateJob(String name, IInfobaseServiceProvider infobaseServiceProvider, InfobaseChangesCache cache,
				IInfobaseApplication application) {
			super(name);
			this.infobaseServiceProvider = infobaseServiceProvider;
			this.cache = cache;
			this.application = application;
		}



		@Override
		protected IStatus run(IProgressMonitor monitor) {
			var changes = infobaseServiceProvider.getChanges(application);
			infobaseChangesCache.clearRecords(application);
			for (String name : changes) {
				infobaseChangesCache.addRecord(application, name);
			}
			System.out.println(changes);
			
			return Status.OK_STATUS;
		}
		
	}

}
