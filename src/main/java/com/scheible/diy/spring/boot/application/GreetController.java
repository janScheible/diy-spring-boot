package com.scheible.diy.spring.boot.application;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author sj
 */
@RestController
class GreetController {
	
	@Value("${foo}")
	String foo;

	@GetMapping("/greet/{name}")
	String greet(@PathVariable String name) throws UnsupportedEncodingException {
		String greet = " Hello " + URLDecoder.decode(name, "UTF-8") + "!!! How are You?";
		return greet;
	}
	
	@GetMapping("/foo")
	String foo() {
		return foo;
	}
}
