package com.vong.manidues.integrated;

import com.vong.manidues.global.data.TestDataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class SpringBootTestBase extends TestDataInitializer {
    protected final TestRestTemplate template;

    @Autowired
    public SpringBootTestBase(
            TestRestTemplate template
    ) {
        this.template = template;
    }

    abstract void initData();

    abstract void setUp();

    abstract void tearDown();
}
