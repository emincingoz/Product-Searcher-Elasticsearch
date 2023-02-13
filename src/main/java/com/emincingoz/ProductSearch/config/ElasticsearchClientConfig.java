package com.emincingoz.ProductSearch.config;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories
@ComponentScan
public class ElasticsearchClientConfig extends AbstractElasticsearchConfiguration {

    /**
     * RestHighLevelClient is deprecated so use RestClient
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/migrate-hlrc.html">...</a>
     */
    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration =
                ClientConfiguration
                        .builder()
                        .connectedTo("localhost:9200")
                        .build();

        // Creates high level rest client
        return RestClients.create(clientConfiguration).rest();
    }
}

//@Configuration
//@EnableElasticsearchRepositories
//@ComponentScan
//public class ElasticsearchClientConfig {
//
//    /**
//     * Configure the URL and port number of the currently running Elasticsearch
//     */
//    @Bean
//    public RestClient getRestClient() {
//        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
//        return restClient;
//    }
//
//    /**
//     * Returns the Transport Object in order to automatically map our Model Class to JSON and integrates them with API Client
//     */
//    @Bean
//    public ElasticsearchTransport getElasticsearchTransport() {
//        return new RestClientTransport(getRestClient(), new JacksonJsonpMapper());
//    }
//
//    /**
//     * It returns an object of ElasticsearchClient, which is further used to perform all query operations with Elasticsearch
//     */
//    @Bean
//    public ElasticsearchClient getElasticsearchClient() {
//        ElasticsearchClient client = new ElasticsearchClient(getElasticsearchTransport());
//        return client;
//    }
//}