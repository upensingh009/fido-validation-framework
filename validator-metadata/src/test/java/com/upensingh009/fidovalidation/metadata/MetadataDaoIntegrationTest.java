package com.upensingh009.fidovalidation.metadata;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.CassandraContainer;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

import com.upensingh009.fidovalidation.metadata.dao.MetadataDao;
import com.upensingh009.fidovalidation.metadata.model.MetadataRecord;
import com.upensingh009.fidovalidation.metadata.service.MetadataService;

import static org.assertj.core.api.Assertions.assertThat;

public class MetadataDaoIntegrationTest {
    private static final String KEYSPACE = "fido";
    private static CassandraContainer<?> cassandra;
    private static CqlSession session;

    @BeforeAll
    public static void startCassandra() throws Exception {
        cassandra = new CassandraContainer<>("cassandra:4.1");
        cassandra.start();

        // wait for port
        Thread.sleep(5000);

        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(cassandra.getHost(), cassandra.getFirstMappedPort()))
                .withLocalDatacenter("dc1")
                .build();

        // create keyspace and table
        session.execute(String.format("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class':'SimpleStrategy','replication_factor':1};", KEYSPACE));
        session.execute(String.format("CREATE TABLE IF NOT EXISTS %s.metadata (id text PRIMARY KEY, json text);", KEYSPACE));
    }

    @AfterAll
    public static void stopCassandra() {
        if (session != null) session.close();
        if (cassandra != null) cassandra.stop();
    }

    @Test
    public void testSaveAndRetrieveMetadata() {
        MetadataDao dao = new MetadataDao(session, KEYSPACE);
        MetadataService service = new MetadataService(dao);

        String id = "test-123";
        String json = "{\"example\":\"metadata\"}";

        service.downloadAndCache(id, json);

        Optional<MetadataRecord> found = service.getMetadata(id);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(id);
        assertThat(found.get().getJson()).isEqualTo(json);
    }
}
