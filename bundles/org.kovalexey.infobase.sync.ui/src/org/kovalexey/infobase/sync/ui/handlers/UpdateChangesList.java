package org.kovalexey.infobase.sync.ui.handlers;

import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kovalexey.infobase.sync.ui.IApplicationsServiceProvider;
import org.kovalexey.infobase.sync.ui.IInfobaseServiceProvider;
import org.kovalexey.infobase.sync.ui.InfobaseSyncListener;

import com._1c.g5.v8.dt.platform.services.core.infobases.sync.IInfobaseSynchronizationListener;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.InfobaseEqualityState;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.InfobaseSynchronizationState;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com.e1c.g5.dt.applications.infobases.IInfobaseApplication;
import com.google.inject.Inject;

public class UpdateChangesList extends AbstractHandler implements IHandler {
	
	@Inject
	IApplicationsServiceProvider applicationsServiceProvider;
	@Inject
	IInfobaseServiceProvider infobaseServiceProvider;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		var viewPart = HandlerUtil.getActivePart(event);
		
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		for (Object selectedObject : selection) {
			if (selectedObject instanceof IInfobaseApplication) {
				IInfobaseApplication infobaseApp = (IInfobaseApplication) selectedObject;
				IProject project = infobaseServiceProvider.getProjectFromInfobaseApplication(infobaseApp);
				InfobaseReference infobase = infobaseApp.getInfobase();
				
				System.out.println(infobaseServiceProvider.getChanges(infobaseApp));
			}
		}
		
		applicationsServiceProvider.updateIfApplicationView(viewPart);
		
		
		return null;
	}

}
