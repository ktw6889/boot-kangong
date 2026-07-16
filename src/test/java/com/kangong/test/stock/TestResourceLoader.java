package com.kangong.test.stock;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestResourceLoader {

	private TestResourceLoader() {
	}

	public static String loadJson(String path) {
		try (InputStream is = TestResourceLoader.class.getClassLoader().getResourceAsStream(path)) {
			if (is == null) throw new IllegalArgumentException("Resource not found: " + path);
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load test resource: " + path, e);
		}
	}
}
