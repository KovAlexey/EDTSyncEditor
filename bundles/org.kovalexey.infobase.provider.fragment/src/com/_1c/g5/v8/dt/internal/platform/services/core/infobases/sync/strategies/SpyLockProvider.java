package com._1c.g5.v8.dt.internal.platform.services.core.infobases.sync.strategies;

import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;

public class SpyLockProvider {
	public static Object getLock(AbstractSynchronizationStrategy strategy, InfobaseReference infobaseReference) {
		return strategy.getLock(infobaseReference);
	}
}
