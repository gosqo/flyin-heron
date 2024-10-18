package com.gosqo.flyinheron.repository;

import com.gosqo.flyinheron.global.data.TestDataInitializer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest(showSql = false)
abstract class RepositoryTestBase extends TestDataInitializer {
}
