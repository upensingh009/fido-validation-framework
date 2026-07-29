package com.upensingh009.fidovalidation.api.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.upensingh009.fidovalidation.metadata.SchemaManager;
import com.upensingh009.fidovalidation.metadata.dao.MetadataDao;
import com.upensingh009.fidovalidation.metadata.service.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;

@Configuration
public class CassandraConfig {
    private static final Logger LOG = LoggerFactory.getLogger(CassandraConfig.class);
    private CqlSession session;

    @Value("${cassandra.contact-points:localhost:9042}")
    private String contactPoints;

    @Value("${cassandra.local-datacenter:dc1}")
    private String localDc;

    @Value("${cassandra.keyspace-name:fido}")
    private String keyspace;

    @Bean
    public CqlSession cqlSession() {
        // parse contact-points (only first entry supported for now)
        StringTokenizer tok = new StringTokenizer(contactPoints, ",");
        String first = tok.nextToken().trim();
        String[] parts = first.split(":");
        String host = parts[0];
        int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9042;

        LOG.info("Creating CqlSession to {}:{} with datacenter {} and keyspace {}", host, port, localDc, keyspace);

        this.session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(localDc)
                .build();

        // ensure schema exists
        SchemaManager schemaManager = new SchemaManager(session, keyspace);
        schemaManager.ensureSchema();

        return this.session;
    }

    @Bean
    public MetadataDao metadataDao(CqlSession session) {
        return new MetadataDao(session, keyspace);
    }

    @Bean
    public MetadataService metadataService(MetadataDao dao) {
        return new MetadataService(dao);
    }

    @PreDestroy
    public void close() {
        if (session != null) {
            LOG.info("Closing CqlSession");
            session.close();
        }
    }
}
