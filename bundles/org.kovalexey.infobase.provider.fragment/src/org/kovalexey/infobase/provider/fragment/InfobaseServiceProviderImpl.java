package org.kovalexey.infobase.provider.fragment;

import java.util.ArrayList;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.kovalexey.infobase.sync.ui.IInfobaseServiceProvider;

import com._1c.g5.v8.bm.core.BmUriUtil;
import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IDtProjectManager;
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
	IDtProjectManager dtProjectManager;
	
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
	public ArrayList<String> getChanges(IInfobaseApplication application) {
		ArrayList<String> result = new ArrayList<String>();
		
		IProject project = getProjectFromInfobaseApplication(application);
		
		var dtProject = dtProjectManager.getDtProject(project);
		ArrayList<IProject> childProjects = getChildProjects(dtProject);
		
		InfobaseReference infobase = application.getInfobase();
		
		addChangesToList(result, infobase, project);
		
		for (IProject iProject : childProjects) {
			addChangesToList(result, infobase, iProject);
		}
		
		return result;
	}

	private void addChangesToList(ArrayList<String> changes, InfobaseReference infobase, IProject iProject) {
		ISynchronizationStrategy strategy;
		strategy = this.strategyProvider.getStrategy(iProject);
		synchronized (SpyLockProvider.getLock((AbstractSynchronizationStrategy)strategy, infobase)) {
			var changedObjects = strategy.getChangedObjects(infobase);
			
			for (EObject eObject : changedObjects) {
				String name = getObjectName(eObject, iProject);
				changes.add(name);
			}
		}
	}

	private ArrayList<IProject> getChildProjects(IDtProject dtProject) {
		var allDtProjects = (ArrayList<IDtProject>)dtProjectManager.getDtProjects();
		
		ArrayList<IProject> childProjects = new ArrayList<IProject>();
		for (IDtProject iDtProject : allDtProjects) {
			var parent = dtProjectManager.findParentProject(iDtProject);
			if (parent == dtProject) {
				childProjects.add(dtProjectManager.getProject(iDtProject));
			}
		}
		return childProjects;
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


}
