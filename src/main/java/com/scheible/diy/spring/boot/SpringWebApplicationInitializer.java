package com.scheible.diy.spring.boot;

import com.scheible.diy.spring.boot.application.DemoApplicationConfig;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Manifest;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 * @author sj
 */
public class SpringWebApplicationInitializer implements WebApplicationInitializer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext webCtx = new AnnotationConfigWebApplicationContext();
		
		webCtx.getEnvironment().getPropertySources().addLast(loadApplicationProperties());
		webCtx.getEnvironment().getPropertySources().addFirst(loadExternalProperties(webCtx.getEnvironment()));
		
		webCtx.addApplicationListener((ContextRefreshedEvent event)
				-> logger.info("Spring " + getSpringVersion(event.getClass()).replace(".RELEASE", "") + " context refreshed"));
		webCtx.register(WebJarsController.class, DemoApplicationConfig.class);
		webCtx.setServletContext(servletContext);
		ServletRegistration.Dynamic dispatcherServletRegistration = servletContext.addServlet("dispatcher", new DispatcherServlet(webCtx));
		dispatcherServletRegistration.setLoadOnStartup(1);
		dispatcherServletRegistration.addMapping("/");

		ServletRegistration.Dynamic defaultServletRegistration = servletContext.addServlet("default", new DefaultServlet());
		defaultServletRegistration.setInitParameter("resourceBase", "./target/classes/static");
		defaultServletRegistration.setLoadOnStartup(1);
		defaultServletRegistration.addMapping("/");
	}

	private PropertiesPropertySource loadApplicationProperties() {
		Properties properties = new Properties();
		
		ClassPathResource applicationPropertiesResource = new ClassPathResource("/config/application.properties", this.getClass().getClassLoader());
		if(applicationPropertiesResource.exists()) {			
			try {
				properties.load(applicationPropertiesResource.getInputStream());
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}			
		}
		
		return new PropertiesPropertySource("application-properties", properties);
	}
	
	private PropertiesPropertySource loadExternalProperties(Environment environment) {
		Properties properties = new Properties();

		String externalLocation = environment.getProperty("spring.config.additional-location");
		if (externalLocation != null) {
			try {
				properties.load(new FileInputStream(ResourceUtils.getFile(externalLocation)));
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		}

		return new PropertiesPropertySource("external-properties", properties);
	}
	
	private String getSpringVersion(Class<?> springClass) {
		if (springClass.getPackage().getImplementationVersion() != null &&
				!springClass.getPackage().getImplementationVersion().equals(getClass().getPackage().getImplementationVersion())) { // NOTE Only available while running in IDE. 
			return springClass.getPackage().getImplementationVersion();
		} else { // NOTE Only available while running as fat JAR.
			try {
				Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
				while (resources.hasMoreElements()) {
					Manifest manifest = new Manifest(resources.nextElement().openStream());
					for (Map.Entry<Object, Object> e : manifest.getMainAttributes().entrySet()) {
						if ("Spring-Version".equals(e.getKey().toString())) {
							return (String) e.getValue();
						}
					}
				}
			} catch (IOException ex) {
			}
		}
		
		return "unknown";
	}
}
