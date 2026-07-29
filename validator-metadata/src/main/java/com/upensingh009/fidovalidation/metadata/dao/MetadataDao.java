package com.upensingh009.fidovalidation.metadata.dao;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.upensingh009.fidovalidation.metadata.model.MetadataRecord;

import java.util.Optional;

/**
 * DAO for metadata records using prepared statements for performance and safety.
 */
public class MetadataDao {
    private final CqlSession session;
    private final String keyspace;
    private final PreparedStatement insertStmt;
    private final PreparedStatement selectStmt;

    public MetadataDao(CqlSession session, String keyspace) {
        this.session = session;
        this.keyspace = keyspace;
        // prepare statements against the provided keyspace
        this.insertStmt = session.prepare(String.format("INSERT INTO %s.metadata (id, json) VALUES (?, ?)", keyspace));
        this.selectStmt = session.prepare(String.format("SELECT id, json FROM %s.metadata WHERE id = ?", keyspace));
    }

    public void save(MetadataRecord record) {
        BoundStatement bound = insertStmt.bind(record.getId(), record.getJson());
        session.execute(bound);
    }

    public Optional<MetadataRecord> findById(String id) {
        BoundStatement bound = selectStmt.bind(id);
        ResultSet rs = session.execute(bound);
        Row r = rs.one();
        if (r == null) return Optional.empty();
        return Optional.of(new MetadataRecord(r.getString("id"), r.getString("json")));
    }
}
