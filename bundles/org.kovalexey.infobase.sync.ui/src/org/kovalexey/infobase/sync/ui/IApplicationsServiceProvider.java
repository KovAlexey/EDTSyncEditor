package org.kovalexey.infobase.sync.ui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonNavigator;

public interface IApplicationsServiceProvider {
	public void doTestForEvent(ExecutionEvent event);
	public void updateIfApplicationView(IWorkbenchPart part);
}
