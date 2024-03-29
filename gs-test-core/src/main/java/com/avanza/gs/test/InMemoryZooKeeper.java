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

import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

/**
 * This starts an in-memory ZooKeeper, and also creates a {@code zoo.cfg} used by GigaSpaces.
 */
final class InMemoryZooKeeper implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(InMemoryZooKeeper.class);

	private final TestingServer zooKeeperServer;
	private final Path zooKeeperConfig;

	public InMemoryZooKeeper() {
		this.zooKeeperServer = startZooKeeper();
		this.zooKeeperConfig = createZooCfg(zooKeeperServer.getPort());
	}

	public Path getZooKeeperConfig() {
		return zooKeeperConfig;
	}

	private static TestingServer startZooKeeper() {
		try {
			return new TestingServer(true);
		} catch (Exception e) {
			throw new RuntimeException("Could not start ZooKeeper test server", e);
		}
	}

	/**
	 * Creates {@code zoo.cfg} that is read by {@link com.gigaspaces.grid.zookeeper.ZookeeperConfig}
	 */
	private static Path createZooCfg(int zooKeeperPort) {
		try (InputStream in = InMemoryZooKeeper.class.getResourceAsStream("/zoo.cfg.template")) {
			String content = StreamUtils.copyToString(in, UTF_8)
					.replace("##CLIENT_PORT##", Integer.toString(zooKeeperPort));

			Path tempFile = Files.createTempFile("zoo", ".cfg");
			return Files.writeString(tempFile, content);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void close() throws Exception {
		zooKeeperServer.close();
		try {
			Files.delete(zooKeeperConfig);
		} catch (IOException e) {
			LOG.warn("Could not delete zoo.cfg at {}", zooKeeperConfig);
		}
	}
}
