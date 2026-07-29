package com.upensingh009.fidovalidation.metadata.dao;

import com.upensingh009.fidovalidation.metadata.model.MetadataRecord;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import java.util.Optional;

public class MetadataDao {
    private final CqlSession session;
    private final String keyspace;

    public MetadataDao(CqlSession session, String keyspace) {
        this.session = session;
        this.keyspace = keyspace;
    }

    public void save(MetadataRecord record) {
        String q = String.format("INSERT INTO %s.metadata (id, json) VALUES (?, ?)", keyspace);
        session.execute(SimpleStatement.newInstance(q, record.getId(), record.getJson()));
    }

    public Optional<MetadataRecord> findById(String id) {
        String q = String.format("SELECT id, json FROM %s.metadata WHERE id = ?", keyspace);
        ResultSet rs = session.execute(SimpleStatement.newInstance(q, id));
        Row r = rs.one();
        if (r == null) return Optional.empty();
        return Optional.of(new MetadataRecord(r.getString("id"), r.getString("json")));
    }
}
