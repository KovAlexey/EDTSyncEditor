package org.kovalexey.infobase.sync.ui.handlers;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com.e1c.g5.dt.applications.infobases.IInfobaseApplication;
import com.google.inject.Inject;

import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.kovalexey.infobase.sync.ui.IInfobaseServiceProvider;


public class SetFullSynchroHandler extends AbstractHandler implements IHandler {
	
	private final static String MESSAGE_QUESTION_REMOVE_ALL_UNSYNC_DATA = "Вы уверены, что хотите очистить данные о зарегистрированных объектах?";
	private final static String MESSAGE_QUESTION_REMOVE_ALL_UNSYNC_DATA_YES = "Подтвердить";
	private final static String MESSAGE_QUESTION_REMOVE_ALL_UNSYNC_DATA_STATUS = "Выполняется очистка данных синхронизации проекта {0} и базы {1}";
	
	@Inject
	IInfobaseServiceProvider infobaseServiceProvider;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		
		for (Object selectedObject : selection) {
			if (selectedObject instanceof IInfobaseApplication) {
				IInfobaseApplication infobaseApp = (IInfobaseApplication) selectedObject;
				IProject project = infobaseServiceProvider.getProjectFromInfobaseApplication(infobaseApp);
				InfobaseReference infobase = infobaseApp.getInfobase();
				
				if (!infobaseServiceProvider.isSynchronized(project, infobase)) {
					// Todo: вывод сообщения
					continue;
				}
				
				if (infobaseServiceProvider.isLoading(project, infobase)) {
					// Todo: вывод сообщения
					continue;
				}
				
				if (infobaseServiceProvider.isEqual(project, infobase)) {
					// Todo: вывод сообщения
					continue;
				}
				
				var questionResult = MessageDialog.openQuestion(
						HandlerUtil.getActiveShell(event), 
						MESSAGE_QUESTION_REMOVE_ALL_UNSYNC_DATA_YES, 
						MESSAGE_QUESTION_REMOVE_ALL_UNSYNC_DATA);
				
				if (!questionResult) {
					return null;
				}
				
				String message = MessageFormat.format(MESSAGE_QUESTION_REMOVE_ALL_UNSYNC_DATA_STATUS, 
						project.getName(),
						infobase.getName());
				
				Job job = Job.create(message, monitor -> {
					infobaseServiceProvider.setInfobaseSynchronized(project, infobase);
				});
				job.setUser(true);
				job.schedule();
				
			}
		}
		
		
		return null;
	}

}
