package org.kovalexey.infobase.sync.ui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchPart;

public interface IApplicationsServiceProvider {
	public void doTestForEvent(ExecutionEvent event);
	public void updateIfApplicationView(IWorkbenchPart part);
}
