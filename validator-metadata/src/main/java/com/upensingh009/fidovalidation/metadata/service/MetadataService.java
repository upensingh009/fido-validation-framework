package com.upensingh009.fidovalidation.metadata.service;

import com.upensingh009.fidovalidation.metadata.dao.MetadataDao;
import com.upensingh009.fidovalidation.metadata.model.MetadataRecord;

import java.util.Optional;

/**
 * Simple metadata service that stores and retrieves metadata blobs in Cassandra via MetadataDao.
 * In production this would implement MDS3 fetching, validation, and caching strategies.
 */
public class MetadataService {
    private final MetadataDao dao;

    public MetadataService(MetadataDao dao) {
        this.dao = dao;
    }

    public void storeMetadata(MetadataRecord record) {
        dao.save(record);
    }

    public Optional<MetadataRecord> getMetadata(String id) {
        return dao.findById(id);
    }

    /**
     * Example stub to download metadata from MDS3 — production implementation would fetch and
     * validate metadata statements; here we simulate by returning a provided payload.
     */
    public MetadataRecord downloadAndCache(String id, String jsonPayload) {
        MetadataRecord r = new MetadataRecord(id, jsonPayload);
        dao.save(r);
        return r;
    }
}
