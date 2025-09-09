package org.kovalexey.infobase.sync.ui.changedrecords;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

public class ChangeRecordLabelProvider extends LabelProvider implements ILabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof ChangeRecord) {
			return ((ChangeRecord)element).getName();
		}
		return super.getText(element);
	}

}
