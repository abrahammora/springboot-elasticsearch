package com.devsamr.elasticsearch.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticSearchConfig {
	
	
	@Bean
	RestClient restClient() {	
		return RestClient.builder(HttpHost.create("http://localhost:9200"))
				.setDefaultHeaders(new Header[]{ new BasicHeader("Content-type", "application/json")})
				.build();
	}

	@Bean
	ElasticsearchTransport transport() {
		return new RestClientTransport(restClient(), new JacksonJsonpMapper());
	}
	
	@Bean
	ElasticsearchClient client() {
		return new ElasticsearchClient(transport());
	}
	
	@Bean
	ElasticsearchAsyncClient asyncClient() {
		return new ElasticsearchAsyncClient(transport());
	}
	
}
