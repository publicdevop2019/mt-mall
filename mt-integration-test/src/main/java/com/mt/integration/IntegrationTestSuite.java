package com.mt.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
