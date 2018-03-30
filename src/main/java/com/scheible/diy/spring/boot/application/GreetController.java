package com.scheible.diy.spring.boot.application;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author sj
 */
@RestController
public class GreetController {

	@GetMapping(path = "/greet/{name}")
	public String greet(@PathVariable String name) throws UnsupportedEncodingException {
		String greet = " Hello " + URLDecoder.decode(name, "UTF-8") + "!!! How are You?";
		return greet;
	}
}
