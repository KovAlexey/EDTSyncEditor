package org.kovalexey.infobase.provider.fragment;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import com._1c.g5.v8.dt.internal.platform.services.core.PlatformServicesCore;
import com._1c.g5.v8.dt.internal.platform.services.core.infobases.sync.InfobaseSynchronization;
import com._1c.g5.v8.dt.internal.platform.services.core.infobases.sync.InfobaseSynchronizationManager;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.IInfobaseSynchronizationManager;
import com._1c.g5.v8.dt.platform.services.core.infobases.sync.strategies.ISynchronizationStrategy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SynchronizationStrategyProvider {
	private final IInfobaseSynchronizationManager infobaseSynchronizationManager;
	
	@Inject
	public SynchronizationStrategyProvider(IInfobaseSynchronizationManager infobaseSynchronizationManager) {
		this.infobaseSynchronizationManager = infobaseSynchronizationManager;
	}
	
	public ISynchronizationStrategy getStrategy(IProject project) {
        InfobaseSynchronization sync = this.getSync(project);
        return sync == null ? null : sync.getStrategy();
    }

    private InfobaseSynchronization getSync(IProject project) {
        if (this.infobaseSynchronizationManager instanceof InfobaseSynchronizationManager) {
            try {
                Field field = InfobaseSynchronizationManager.class.getDeclaredField("synchronizations");
                field.setAccessible(true);
                Object value = field.get(this.infobaseSynchronizationManager);
                if (value instanceof Map) {
                    Object sync = ((Map<?, ?>)value).get(project);
                    if (sync instanceof InfobaseSynchronization) {
                        return (InfobaseSynchronization)sync;
                    }
                }
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | SecurityException e) {
                PlatformServicesCore.logError("Application change content is not available due API changed", e);
            }
        }

        return null;
    }
}
