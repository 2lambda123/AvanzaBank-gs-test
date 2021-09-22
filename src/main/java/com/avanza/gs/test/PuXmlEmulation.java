/*
 * Copyright 2017 Avanza Bank AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avanza.gs.test;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.util.StreamUtils;

public class PuXmlEmulation {
	public static Path createPuXmlConf(Class<?> puConfig) {
		try (InputStream in = PuXmlEmulation.class.getResourceAsStream("/pu.xml.template")) {
			Path tempFile = Files.createTempFile("config", ".xml");
			String content = StreamUtils.copyToString(in, UTF_8)
					.replace("##CLASSNAME##", puConfig.getName());
			Files.write(tempFile, content.getBytes(UTF_8));
			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
