package com.upensingh009.fidovalidation.api.controller;

import com.upensingh009.fidovalidation.common.dto.BaseResponse;
import com.upensingh009.fidovalidation.common.dto.MetadataDto;
import com.upensingh009.fidovalidation.metadata.model.MetadataRecord;
import com.upensingh009.fidovalidation.metadata.service.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/validate/metadata")
@Validated
public class MetadataController {
    private static final Logger LOG = LoggerFactory.getLogger(MetadataController.class);
    private final MetadataService metadataService;

    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<MetadataDto>> store(@Valid @RequestBody MetadataDto dto) {
        LOG.info("Received metadata store request for id={}", dto.id());
        MetadataRecord rec = metadataService.downloadAndCache(dto.id(), dto.json());
        MetadataDto resp = new MetadataDto(rec.getId(), rec.getJson());
        return ResponseEntity.created(URI.create(String.format("/validate/metadata/%s", rec.getId())))
                .body(new BaseResponse<>("OK", resp));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<MetadataDto>> get(@PathVariable("id") @NotBlank String id) {
        LOG.info("Get metadata for id={}", id);
        Optional<MetadataRecord> found = metadataService.getMetadata(id);
        return found.map(r -> ResponseEntity.ok(new BaseResponse<>("OK", new MetadataDto(r.getId(), r.getJson()))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
