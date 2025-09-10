package org.kovalexey.infobase.sync.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kovalexey.infobase.sync.ui.IApplicationsServiceProvider;
import org.kovalexey.infobase.sync.ui.IInfobaseServiceProvider;
import com.e1c.g5.dt.applications.infobases.IInfobaseApplication;
import com.google.inject.Inject;

public class UpdateChangesList extends AbstractHandler implements IHandler {
	
	@Inject
	IApplicationsServiceProvider applicationsServiceProvider;
	@Inject
	IInfobaseServiceProvider infobaseServiceProvider;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO: реализация
		var viewPart = HandlerUtil.getActivePart(event);
		
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		for (Object selectedObject : selection) {
			if (selectedObject instanceof IInfobaseApplication) {
				IInfobaseApplication infobaseApp = (IInfobaseApplication) selectedObject;
				
				infobaseServiceProvider.getChanges(infobaseApp);
			}
		}
		
		applicationsServiceProvider.updateIfApplicationView(viewPart);
		
		
		return null;
	}

}
