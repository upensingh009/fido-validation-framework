package com.upensingh009.fidovalidation.metadata.model;

import java.util.Objects;

public class MetadataRecord {
    private final String id;
    private final String json;

    public MetadataRecord(String id, String json) {
        this.id = id;
        this.json = json;
    }

    public String getId() {
        return id;
    }

    public String getJson() {
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetadataRecord that = (MetadataRecord) o;
        return Objects.equals(id, that.id) && Objects.equals(json, that.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, json);
    }

    @Override
    public String toString() {
        return "MetadataRecord{" +
                "id='" + id + '\'' +
                ", json='" + (json == null ? "<null>" : json.substring(0, Math.min(40, json.length())) + "...'") +
                '}';
    }
}
