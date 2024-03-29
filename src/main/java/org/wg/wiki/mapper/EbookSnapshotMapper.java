package org.wg.wiki.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.wg.wiki.model.entity.EbookSnapshot;
import org.wg.wiki.model.entity.EbookSnapshotExample;

public interface EbookSnapshotMapper {
    long countByExample(EbookSnapshotExample example);

    int deleteByExample(EbookSnapshotExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EbookSnapshot record);

    int insertSelective(EbookSnapshot record);

    List<EbookSnapshot> selectByExample(EbookSnapshotExample example);

    EbookSnapshot selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EbookSnapshot record, @Param("example") EbookSnapshotExample example);

    int updateByExample(@Param("record") EbookSnapshot record, @Param("example") EbookSnapshotExample example);

    int updateByPrimaryKeySelective(EbookSnapshot record);

    int updateByPrimaryKey(EbookSnapshot record);

    void generateSnapshot();

    List<EbookSnapshot> get2day();

    List<EbookSnapshot> get30day();
}