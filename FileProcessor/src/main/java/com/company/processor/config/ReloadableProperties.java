package com.company.processor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expose an endpoint, refreshing which fresh values from the properties will be
 * used
 * Hit to refersh: localhost:7000/actuator/refresh
 */
@RestController
@RefreshScope
public class ReloadableProperties {
	@Autowired
	Environment env;
}
