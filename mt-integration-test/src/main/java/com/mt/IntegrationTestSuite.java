package com.mt;

import com.mt.integration.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        AddressTest.class,
        CartTest.class,
        CatalogTest.class,
        OrderTest.class,
        ProductTest.class,
        ProductConcurrentTest.class,
        ProductQueryTest.class,
        TagTest.class,

})
public class IntegrationTestSuite {
}
