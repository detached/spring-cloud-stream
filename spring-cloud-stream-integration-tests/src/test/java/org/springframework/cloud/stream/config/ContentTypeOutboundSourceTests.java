/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.cloud.stream.config;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.stream.annotation.Bindings;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.TestSupportBinder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Ilayaperumal Gopinathan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({ContentTypeOutboundSourceTests.TestSource.class})
public class ContentTypeOutboundSourceTests {

	@Autowired
	@Bindings(TestSource.class)
	private Source testSource;

	@Autowired
	private BinderFactory binderFactory;

	@Test
	@SuppressWarnings("unchecked")
	public void testMessageHeaderWhenNoExplicitContentTypeOnMessage() throws Exception {
		testSource.output().send(MessageBuilder.withPayload("{\"message\":\"Hi\"}").build());
		Message<String> received = (Message<String>) ((TestSupportBinder) binderFactory.getBinder(null))
				.messageCollector().forChannel(testSource.output()).poll();
		assertThat(received.getHeaders().get(MessageHeaders.CONTENT_TYPE).toString()).isEqualTo("application/json");
		assertThat(received).hasFieldOrPropertyWithValue("payload", "{\"message\":\"Hi\"}");
	}

	@EnableBinding(Source.class)
	@EnableAutoConfiguration
	@PropertySource("classpath:/org/springframework/cloud/stream/config/channel/source-channel-configurers.properties")
	public static class TestSource {

	}
}

