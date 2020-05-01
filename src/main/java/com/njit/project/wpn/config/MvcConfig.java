package com.njit.project.wpn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("login");
		registry.addViewController("/register").setViewName("register");
		registry.addViewController("/home").setViewName("home");
		registry.addViewController("/loginFailure").setViewName("loginFailure");
		registry.addViewController("/login").setViewName("login");
	}

}
