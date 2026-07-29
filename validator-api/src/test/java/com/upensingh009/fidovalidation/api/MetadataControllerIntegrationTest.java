package com.upensingh009.fidovalidation.api;

import com.upensingh009.fidovalidation.common.dto.BaseResponse;
import com.upensingh009.fidovalidation.common.dto.MetadataDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class MetadataControllerIntegrationTest {
    @LocalServerPort
    int port;

    private static final String KEYSPACE = "fido";

    @Container
    public static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1");

    @DynamicPropertySource
    static void registerCassandraProperties(DynamicPropertyRegistry registry) {
        String contact = String.format("%s:%d", cassandra.getHost(), cassandra.getFirstMappedPort());
        registry.add("cassandra.contact-points", () -> contact);
        registry.add("cassandra.local-datacenter", () -> "dc1");
        registry.add("cassandra.keyspace-name", () -> KEYSPACE);
    }

    @BeforeAll
    public static void waitForCassandra() throws InterruptedException {
        // give Cassandra a moment to be ready for schema creation (Cassandra container uses wait strategies but we keep a short sleep)
        Thread.sleep(5000);
    }

    @AfterAll
    public static void stop() {
        // container lifecycle is managed by Testcontainers
    }

    @Test
    public void testStoreAndGetMetadataViaApi() throws Exception {
        RestTemplate rest = new RestTemplate();
        String base = String.format("http://localhost:%d/validate/metadata", port);

        MetadataDto dto = new MetadataDto("api-test-1", "{\"hello\":\"world\"}");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MetadataDto> req = new HttpEntity<>(dto, headers);

        ResponseEntity<BaseResponse> resp = rest.postForEntity(new URI(base), req, BaseResponse.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();

        // GET
        ResponseEntity<BaseResponse> get = rest.getForEntity(new URI(base + "/" + dto.id()), BaseResponse.class);
        assertThat(get.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
