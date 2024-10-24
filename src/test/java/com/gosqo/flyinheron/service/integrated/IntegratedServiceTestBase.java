package com.gosqo.flyinheron.service.integrated;

import com.gosqo.flyinheron.global.data.TestDataInitializer;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
abstract class IntegratedServiceTestBase extends TestDataInitializer {

    private final TestDataRemover remover;

    public IntegratedServiceTestBase(TestDataRemover remover) {
        this.remover = remover;
    }

    @AfterEach
    void tearDown() {
        remover.removeAll();
    }
}
