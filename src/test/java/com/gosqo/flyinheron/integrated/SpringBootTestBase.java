package com.gosqo.flyinheron.integrated;

import com.gosqo.flyinheron.global.data.TestDataInitializer;
import com.gosqo.flyinheron.global.data.TestDataRemover;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class SpringBootTestBase extends TestDataInitializer {
    protected final TestRestTemplate template;
    protected final TestDataRemover remover;

    public SpringBootTestBase(
            TestRestTemplate template
            , TestDataRemover remover
    ) {
        this.template = template;
        this.remover = remover;
    }

    abstract void initData();

    abstract void setUp();

    @AfterEach
    void tearDown() {
        remover.removeAll();
    }

    ;
}
