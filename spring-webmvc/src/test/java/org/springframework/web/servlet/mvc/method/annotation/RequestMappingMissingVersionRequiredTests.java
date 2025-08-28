/*
 * Copyright 2002-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.testfixture.servlet.MockHttpServletRequest;
import org.springframework.web.testfixture.servlet.MockHttpServletResponse;
import org.springframework.web.testfixture.servlet.MockServletConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for API versioning when version is required and missing.
 */
public class RequestMappingMissingVersionRequiredTests {

	private DispatcherServlet dispatcherServlet;

	@BeforeEach
	void setUp() throws ServletException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setServletConfig(new MockServletConfig());
		context.register(WebConfig.class, TestController.class);
		context.afterPropertiesSet();

		this.dispatcherServlet = new DispatcherServlet(context);
		this.dispatcherServlet.init(new MockServletConfig());
	}

	@Test
	void missingVersionWhenRequiredResultsInBadRequest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
		MockHttpServletResponse response = new MockHttpServletResponse();
		this.dispatcherServlet.service(request, response);
		assertThat(response.getStatus()).isEqualTo(400);
	}

	@EnableWebMvc
	private static class WebConfig implements WebMvcConfigurer {

		@Override
		public void configureApiVersioning(ApiVersionConfigurer configurer) {
			configurer
					.useRequestHeader("X-API-Version")
					.setVersionRequired(true);
		}
	}

	@RestController
	private static class TestController {

		@GetMapping
		String noVersion() {
			return "ok";
		}
	}
}
