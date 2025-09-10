package org.kovalexey.infobase.sync.ui;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kovalexey.infobase.sync.ui.changedrecords.ChangeRecord;

import com.e1c.g5.dt.applications.IApplication;

@Singleton
public class InfobaseChangesCache {

	private Map<IApplication, ArrayList<ChangeRecord>> changes = new ConcurrentHashMap<IApplication, ArrayList<ChangeRecord>>();
	
	public void addRecord(ChangeRecord record) {
		var value = changes.computeIfAbsent(record.getApplication(), (k) -> new ArrayList<ChangeRecord>());
		synchronized (value) {
			value.add(record);
		}
	}
	
	public void clearRecords(IApplication app) {
		var value = changes.get(app);
		if (value == null) {
			return;
		}
		changes.remove(app);
	}
	
	public synchronized ArrayList<ChangeRecord> getRecords(IApplication app) {
		var changeList = changes.get(app);
		if (changeList == null) {
			return changeList;
		}
		synchronized (changeList) {
			return new ArrayList<ChangeRecord>(changeList);
		}
	}
}
