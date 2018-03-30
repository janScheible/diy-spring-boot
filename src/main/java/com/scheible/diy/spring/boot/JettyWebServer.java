package com.scheible.diy.spring.boot;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.AnnotationConfiguration.ClassInheritanceMap;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.web.WebApplicationInitializer;

/**
 *
 * @author sj
 */
public class JettyWebServer {

	public static void main(String[] args) throws Exception {
		final Server server = new Server(8080);
		server.setStopAtShutdown(true);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/diy");
		webAppContext.setAttribute(AnnotationConfiguration.CLASS_INHERITANCE_MAP, createClassMap());
		webAppContext.setConfigurations(new Configuration[]{new AnnotationConfiguration()});
		server.setHandler(webAppContext);

		server.start();
		server.join();
	}

	private static ClassInheritanceMap createClassMap() {
		ClassInheritanceMap classMap = new ClassInheritanceMap();
		Set<String> impl = ConcurrentHashMap.newKeySet();
		impl.add(SpringWebApplicationInitializer.class.getName());
		classMap.put(WebApplicationInitializer.class.getName(), impl);
		return classMap;
	}
}
