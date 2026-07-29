package com.upensingh009.fidovalidation.metadata;

import com.datastax.oss.driver.api.core.CqlSession;

/**
 * Utility to ensure keyspace and tables exist. Intended to be used at application startup
 * or in tests to bootstrap schema.
 */
public class SchemaManager {
    private final CqlSession session;
    private final String keyspace;

    public SchemaManager(CqlSession session, String keyspace) {
        this.session = session;
        this.keyspace = keyspace;
    }

    public void ensureSchema() {
        session.execute(String.format("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class':'SimpleStrategy','replication_factor':1};", keyspace));
        session.execute(String.format("CREATE TABLE IF NOT EXISTS %s.metadata (id text PRIMARY KEY, json text);", keyspace));
    }
}
