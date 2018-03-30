package com.scheible.diy.spring.boot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

/**
 *
 * @author sj
 */
@Controller
public class WebJarsController {

	@GetMapping(path = "/webjars/**")
	@ResponseBody
	public ResponseEntity<String> greet(HttpServletRequest request) throws IOException {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		AntPathMatcher apm = new AntPathMatcher();
		String filePath = apm.extractPathWithinPattern(bestMatchPattern, path);

		ClassPathResource webJarsFile = new ClassPathResource("/META-INF/resources/webjars/" + filePath);
		if (!webJarsFile.exists()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(readFile(webJarsFile.getInputStream()), HttpStatus.OK);
		}
	}

	private String readFile(InputStream input) throws IOException {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF8")))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}
}
