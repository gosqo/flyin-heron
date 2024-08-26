package com.vong.manidues.repository;

import com.vong.manidues.global.config.JpaAuditingConfig;
import com.vong.manidues.global.data.TestDataInitializer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest(showSql = false)
@Import(JpaAuditingConfig.class)
abstract class RepositoryTestBase extends TestDataInitializer {

    abstract void initData();

    abstract void setUp();
}
