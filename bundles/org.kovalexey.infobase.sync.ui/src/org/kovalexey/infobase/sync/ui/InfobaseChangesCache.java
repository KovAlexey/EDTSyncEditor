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
	
	public void addRecord(IApplication app, String name) {
		var value = changes.computeIfAbsent(app, (k) -> new ArrayList<ChangeRecord>());
		value.add(new ChangeRecord(name, app));
	}
	
	public void clearRecords(IApplication app) {
		if (changes.get(app) != null) {
			changes.get(app).clear();
		}
	}
	
	public ArrayList<ChangeRecord> getRecords(IApplication app) {
		return changes.get(app);
	}
}
