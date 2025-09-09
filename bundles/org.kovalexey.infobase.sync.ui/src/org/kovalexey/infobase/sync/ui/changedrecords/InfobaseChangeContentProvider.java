package org.kovalexey.infobase.sync.ui.changedrecords;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.kovalexey.infobase.sync.ui.InfobaseChangesCache;

import com.e1c.g5.dt.applications.IApplication;
import com.e1c.g5.dt.applications.infobases.IInfobaseApplication;
import com.google.inject.Inject;

public class InfobaseChangeContentProvider implements ITreeContentProvider {
	
	@Inject
	InfobaseChangesCache infobaseChangesCache;

	@Override
	public Object[] getElements(Object inputElement) {
		System.out.println("getElements");
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IApplication) {
			return ((Object[])infobaseChangesCache.getRecords((IApplication)parentElement).toArray());
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		System.out.println("getParent");
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IApplication) {
			return (infobaseChangesCache.getRecords((IApplication)element).size() > 0);
		}
		return false;
	}

}
