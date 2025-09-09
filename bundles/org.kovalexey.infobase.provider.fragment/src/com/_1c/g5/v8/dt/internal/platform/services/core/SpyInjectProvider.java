package com._1c.g5.v8.dt.internal.platform.services.core;

import com.google.inject.Injector;

public class SpyInjectProvider {
	public static Injector getCoreInjector() {
		return PlatformServicesCore.getDefault().getInjector();
	}
}
