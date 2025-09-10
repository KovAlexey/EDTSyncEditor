package org.kovalexey.infobase.sync.ui.changedrecords;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.kovalexey.infobase.sync.ui.InfobaseChangesCache;

import com.e1c.g5.dt.applications.IApplication;
import com.google.inject.Inject;

public class InfobaseChangeContentProvider implements ITreeContentProvider {
	
	@Inject
	InfobaseChangesCache infobaseChangesCache;

	@Override
	public Object[] getElements(Object inputElement) {
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IApplication) {
			var records = infobaseChangesCache.getRecords((IApplication)parentElement);
			if (records == null) {
				return null;
			}
			return ((Object[])records.toArray());
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IApplication) {
			var records = infobaseChangesCache.getRecords((IApplication)element);
			if (records == null) {
				return false;
			}
			return (records.size() > 0);
		}
		return false;
	}

}
