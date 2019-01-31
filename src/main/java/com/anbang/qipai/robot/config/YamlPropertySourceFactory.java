package com.anbang.qipai.robot.config;

import java.io.IOException;

import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

public class YamlPropertySourceFactory implements PropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
		return name != null ? new PropertySourcesLoader().load(resource.getResource(), name, null)
				: new PropertySourcesLoader().load(resource.getResource(), getNameForResource(resource.getResource()),
						null);
	}

	private static String getNameForResource(Resource resource) {
		String name = resource.getDescription();
		if (!org.springframework.util.StringUtils.hasText(name)) {
			name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
		}
		return name;
	}

}
