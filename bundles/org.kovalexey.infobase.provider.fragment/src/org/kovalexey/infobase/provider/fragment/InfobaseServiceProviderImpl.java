package org.kovalexey.infobase.provider.fragment;

import java.util.ArrayList;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.kovalexey.infobase.sync.ui.IInfobaseServiceProvider;
import org.kovalexey.infobase.sync.ui.changedrecords.ChangeRecord;

import com._1c.g5.v8.bm.core.BmUriUtil;
import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IV8ProjectProvider;
import com._1c.g5.v8.dt.core.platform.IWorkspaceOrchestrator;
import com._1c.g5.v8.dt.internal.platform.services.core.SpyInjectProvider;
import com._1c.g5.v8.dt.internal.platform.services.core.infobases.sync.strategies.AbstractSynchronizationStrategy;
import com._1c.g5.v8.dt.internal.platform.services.core.infobases.sync.strategies.SpyLockProvider;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseAssociationManager;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.IInfobaseSynchronizationListener;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.IInfobaseSynchronizationManager;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.InfobaseEqualityState;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.InfobaseSynchronizationException;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.InfobaseSynchronizationState;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.strategies.ISynchronizationStrategy;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com.e1c.g5.dt.applications.IApplication;
import com.e1c.g5.dt.applications.IApplicationManager;
import com.e1c.g5.dt.applications.infobases.IInfobaseApplication;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class InfobaseServiceProviderImpl implements IInfobaseServiceProvider {

	@Inject
	IInfobaseSynchronizationManager syncManager;
	@Inject
	SynchronizationStrategyProvider strategyProvider;
	@Inject
	IInfobaseAssociationManager iInfobaseAssociationManager;
	@Inject
	IApplicationManager applicationManager;
	@Inject
	IWorkspaceOrchestrator workspaceOrchestrator;
	@Inject
	IV8ProjectProvider v8ProjectProvider;
	
	@Override
	public Boolean isSynchronized(IProject project, InfobaseReference infobase) {
		InfobaseSynchronizationState state = this.syncManager.getSynchronizationState(project, infobase);
		return (state == InfobaseSynchronizationState.SYNCHRONIZED);
	}

	@Override
	public Boolean isEqual(IProject project, InfobaseReference infobase) {
		InfobaseEqualityState equality = this.syncManager.getEqualityState(project, infobase);
		return (equality == InfobaseEqualityState.EQUAL);
	}



	@Override
	public Boolean isLoading(IProject project, InfobaseReference infobase) {
		InfobaseEqualityState equality = this.syncManager.getEqualityState(project, infobase);
		InfobaseSynchronizationState state = this.syncManager.getSynchronizationState(project, infobase);
		return (equality == InfobaseEqualityState.LOADING || state == InfobaseSynchronizationState.SYNCHRONIZING);
	}



	@Override
	public void setInfobaseSynchronized(IProject project, InfobaseReference infobase, IProgressMonitor monitor) {
		ISynchronizationStrategy strategy = this.strategyProvider.getStrategy(project);
		strategy.fullReloadRequested(infobase, monitor);
		try {
			this.syncManager.setSynchronizationState(project, infobase, InfobaseSynchronizationState.SYNCHRONIZED);
		} catch (InfobaseSynchronizationException e) {
			// TODO: Обработать исключени? Сделать вывод?
			e.printStackTrace();
		}
	}



	@Override
	public void setInfobaseSynchronized(IProject project, InfobaseReference infobase) {
		setInfobaseSynchronized(project, infobase, null);
	}



	@Override
	public IProject getProjectFromInfobaseApplication(IInfobaseApplication application) {
		return application.getProject();
	}

	@Override
	public Injector getCoreInjector() {
		return SpyInjectProvider.getCoreInjector();
	}

	@Override
	public void addSynchronizationListener(IInfobaseSynchronizationListener listener) {
		syncManager.addInfobaseSynchronizationListener(listener);
	}

	@Override
	public ArrayList<ChangeRecord> getChanges(IInfobaseApplication application) {
		ArrayList<ChangeRecord> result = new ArrayList<ChangeRecord>();
		
		IProject project = getProjectFromInfobaseApplication(application);
		var childProjects = v8ProjectProvider.getDependentProjects(project);
		
		InfobaseReference infobase = application.getInfobase();
		
		addChangesToList(result, infobase, project, application);
		
		for (IProject childProject : childProjects) {
			addChangesToList(result, infobase, childProject, application);
		}
		
		return result;
	}
	
	@Override
	public IInfobaseApplication getApplicationFromInfobase(InfobaseReference infobase) {
		var application = applicationManager.findApplicationByInfobase(infobase);
		if (!application.isPresent()) {
			return null;
		}
		
		if (application.get() instanceof IInfobaseApplication) {
			var infobaseApplication = ((IInfobaseApplication)application.get());
			return infobaseApplication;
		}
		
		return null;
	}

	private void addChangesToList(ArrayList<ChangeRecord> result, InfobaseReference infobase, IProject iProject, IApplication application) {
		ISynchronizationStrategy strategy;
		strategy = this.strategyProvider.getStrategy(iProject);
		if (strategy == null) {
			// У внешних обработок и т.д. стратегий синхронизации нет
			return;
		}
		synchronized (SpyLockProvider.getLock((AbstractSynchronizationStrategy)strategy, infobase)) {
			var changedObjects = strategy.getChangedObjects(infobase);
			
			for (EObject eObject : changedObjects) {
				String name = getObjectName(eObject, iProject);
				
				ChangeRecord record = new ChangeRecord(name, application, iProject);
				
				result.add(record);
			}
		}
	}
	
	private String getObjectName(EObject object, IProject project) {
        String name = null;
        if (object instanceof IBmObject && ((IBmObject)object).bmIsTop() && ((IBmObject)object).bmGetNamespace() != null) {
            name = ((IBmObject)object).bmGetFqn();
        }

        if (name == null) {
            URI uri = EcoreUtil.getURI(object);
            if (BmUriUtil.isBmUri(uri)) {
                name = uri.path();
            } else if (uri.isPlatformResource()) {
                name = uri.toPlatformString(true);
            } else {
                name = uri.path();
            }
        }

        if (name == null) {
            name = object.toString();
        }

        return name;
    }

}
