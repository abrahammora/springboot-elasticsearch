package com.devsamr.elasticsearch;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devsamr.elasticsearch.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.JsonData;

@SpringBootTest
class DevsamrElasticsearchApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(DevsamrElasticsearchApplicationTests.class);

	@Autowired
	private ElasticsearchClient client;
	@Autowired
	private ElasticsearchAsyncClient asyncClient;

//	@Test
//	void createIndex() throws ElasticsearchException, IOException {
//		CreateIndexResponse createIndexResponse =  client.indices().create( c -> c
//			.index("users")
//		);
//		log.info("Created Index = {} ", createIndexResponse.index());
//	}

	@Test
	void UsingthefluentDSL() throws ElasticsearchException, IOException {
		Faker faker = new Faker();
		User user = new User();
		user.setId(faker.number().randomNumber());
		user.setNombre(faker.name().firstName());
		user.setApellido(faker.name().lastName());
		user.setUsername(faker.name().username());
//		IndexResponse response = client
//				.index(i -> i.index("users")
//				.id(String.format("%s-%s",String.valueOf(user.getId()), user.getUsername()))
//				.document(user));

		IndexRequest<User> request = IndexRequest.of(i -> i.index("users")
				.id(String.format("%s-%s", String.valueOf(user.getId()), user.getUsername())).document(user));

		IndexResponse response = client.index(request);
		log.info("Indexed with version " + response.version());

	}

	@Test
	public void UsingClassicBuilders() throws ElasticsearchException, IOException {
		Faker faker = new Faker();
		User user = new User();
		user.setId(faker.number().randomNumber());
		user.setNombre(faker.name().firstName());
		user.setApellido(faker.name().lastName());
		user.setUsername(faker.name().username());

		IndexRequest.Builder<User> builder = new IndexRequest.Builder<>();
		builder.index("users");
		builder.id(String.format("%s-%s", String.valueOf(user.getId()), user.getUsername()));
		builder.document(user);

		IndexResponse response = client.index(builder.build());

		log.info("Indexed with version " + response.version());
	}

	@Test
	public void UsingTheAsynchronousClient() {
		Faker faker = new Faker();
		User user = new User();
		user.setId(faker.number().randomNumber());
		user.setNombre(faker.name().firstName());
		user.setApellido(faker.name().lastName());
		user.setUsername(faker.name().username());
		asyncClient
				.index(i -> i.index("users")
						.id(String.format("%s-%s", String.valueOf(user.getId()), user.getUsername())).document(user))
				.whenComplete((response, exception) -> {
					if (exception != null) {
						log.error("Failed to index", exception);
					} else {
						log.info("Indexed with version " + response.version());
					}
				});
	}

	@Test
	public void UsingRawJSONData() throws ElasticsearchException, IOException {
		Faker faker = new Faker();
		User user = new User();
		user.setId(faker.number().randomNumber());
		user.setNombre(faker.name().firstName());
		user.setApellido(faker.name().lastName());
		user.setUsername(faker.name().username());
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);
		log.info(json);
		IndexRequest<JsonData> indexRequest = IndexRequest.of(i -> i.index("users").withJson(new StringReader(json)));

		IndexResponse response = client.index(indexRequest);

		log.info("Indexed with version " + response.version());
	}
}
