package org.kovalexey.infobase.applications.ui.fragment;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kovalexey.infobase.sync.ui.IApplicationsServiceProvider;

import com.e1c.g5.dt.internal.applications.ui.view.ApplicationsView;

public class AppicationsServiceProvider implements IApplicationsServiceProvider {

	@Override
	public void doTestForEvent(ExecutionEvent event) {
		ApplicationsView view = (ApplicationsView) HandlerUtil.getActivePart(event);
		view.updateViewUsingCurrentSelection();
		System.out.println(view);
	}

	@Override
	public void updateIfApplicationView(IWorkbenchPart part) {
		if (part instanceof ApplicationsView) {
			var applicationView = (ApplicationsView) part;
			applicationView.updateViewUsingCurrentSelection();
		}
		
	}

}
