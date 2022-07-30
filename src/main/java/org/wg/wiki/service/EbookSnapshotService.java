package org.wg.wiki.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.wg.wiki.mapper.EbookSnapshotMapper;
import org.wg.wiki.model.entity.EbookSnapshot;

import java.util.List;

@Service
public class EbookSnapshotService {

    private static final Logger logger = LoggerFactory.getLogger(EbookSnapshotService.class);

    @Autowired
    private EbookSnapshotMapper ebookSnapshotMapper;

    public List<EbookSnapshot> get2day() {
        return ebookSnapshotMapper.get2day();
    }

    public List<EbookSnapshot> get30day() {
        return ebookSnapshotMapper.get30day();
    }
}
