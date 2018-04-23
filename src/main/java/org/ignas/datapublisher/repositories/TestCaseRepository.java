package org.ignas.datapublisher.repositories;

import org.ignas.datapublisher.TestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestCaseRepository extends CrudRepository<TestCase, Long> {

    List<TestCase> findByProcessedIsFalseOrderByIdAsc(Pageable pageable);

    @Modifying
    @Query("UPDATE TestCase SET processed = true WHERE id <= :id and processed = false ")
    int updateProcessed(@Param("id") Long id);
}
