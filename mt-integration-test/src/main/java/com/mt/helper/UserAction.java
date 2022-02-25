package com.mt.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.runner.Description;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

@Component
@Slf4j
public class UserAction {
    public static final String TEST_TEST_VALUE = "3T8BRPK17W8A:S";
    public static final String TEST_TEST_VALUE_2 = "3T8BRPK17W94:上装";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String CLIENT_ID_LOGIN_ID = "0C8AZZ16LZB4";
    public static final String CLIENT_ID_REGISTER_ID = "0C8B00098WLD";
    public static final String CLIENT_ID_USER_PROFILE_ID = "0C8AZTODP4H7";
    public static final String CLIENT_ID_SAGA_ID = "0C8AZTODP4H1";
    public static final String COMMON_CLIENT_SECRET = "root";
    public static final String EMPTY_CLIENT_SECRET = "";
    public static final String ACCOUNT_USERNAME_ADMIN = "mall@duoshu.org";
    public static final String ACCOUNT_PASSWORD_ADMIN = "root";
    public static final String ACCOUNT_USERNAME_USER = "user@duoshu.org";
    public static final String ACCOUNT_PASSWORD_USER = "root";
    public static final String SVC_NAME_AUTH = "/auth-svc";
    public static final String SVC_NAME_PRODUCT = "/product-svc";
    public static final String SVC_NAME_PROFILE = "/profile-svc";
    public static String proxyUrl = "http://192.168.2.23:" + 8111;
    public static final String URL = UserAction.proxyUrl + SVC_NAME_AUTH + "/oauth/token";
    public static String PROXY_URL_TOKEN = proxyUrl + SVC_NAME_AUTH + "/oauth/token";
    public List<ResourceOwner> testUser = new ArrayList<>();
    public ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    public TestRestTemplate restTemplate = new TestRestTemplate();
    @Autowired
    private TestHelper helper;
    @Value("${register_real_user}")
    private boolean registerRealUser;

    public UserAction() {
    }

    /**
     * copied from https://www.planetgeek.ch/2009/08/25/how-to-find-a-concurrency-bug-with-java/
     *
     * @param message
     * @param runnables
     * @param maxTimeoutSeconds
     * @throws InterruptedException
     */
    public static void assertConcurrent(final String message, final List<? extends Runnable> runnables, final int maxTimeoutSeconds) throws InterruptedException {
        final int numThreads = runnables.size();
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
            final CountDownLatch afterInitBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            for (final Runnable submittedTestRunnable : runnables) {
                threadPool.submit(new Runnable() {
                    public void run() {
                        allExecutorThreadsReady.countDown();
                        try {
                            afterInitBlocker.await();
                            submittedTestRunnable.run();
                        } catch (final Throwable e) {
                            exceptions.add(e);
                        } finally {
                            allDone.countDown();
                        }
                    }
                });
            }
            // wait until all threads are ready
            assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent", allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            assertTrue(message + " timeout! More than" + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
        } finally {
            threadPool.shutdownNow();
        }
        assertTrue(message + "failed with exception(s)" + exceptions, exceptions.isEmpty());
    }

    public void saveResult(Description description, UUID uuid) {
//        FailedRecord failedRecord = new FailedRecord();
//        failedRecord.setFailedTestMethod(description.getMethodName());
//        failedRecord.setUuid(uuid.toString());
//        failedRecordRepo.save(failedRecord);
    }

    public void initTestUser() {
        if (testUser.size() == 0) {
            log.debug("start of creating test users");
            ResourceOwner resourceOwner1 = randomRegisterAnUser();
            ResourceOwner resourceOwner2 = randomRegisterAnUser();
            ResourceOwner resourceOwner3 = randomRegisterAnUser();
            ResourceOwner resourceOwner4 = randomRegisterAnUser();
            ResourceOwner resourceOwner5 = randomRegisterAnUser();
            ResourceOwner resourceOwner6 = randomRegisterAnUser();
            ResourceOwner resourceOwner7 = randomRegisterAnUser();
            ResourceOwner resourceOwner8 = randomRegisterAnUser();
            ResourceOwner resourceOwner9 = randomRegisterAnUser();
            ResourceOwner resourceOwner10 = randomRegisterAnUser();
            testUser.add(resourceOwner1);
            testUser.add(resourceOwner2);
            testUser.add(resourceOwner3);
            testUser.add(resourceOwner4);
            testUser.add(resourceOwner5);
            testUser.add(resourceOwner6);
            testUser.add(resourceOwner7);
            testUser.add(resourceOwner8);
            testUser.add(resourceOwner9);
            testUser.add(resourceOwner10);
            log.debug("end of creating test users");
        } else {
            log.debug("test users already exist");

        }
    }

    public ResourceOwner randomCreateUserDraft() {
        return userCreateDraft(UUID.randomUUID().toString().replace("-", "") + "@gmail.com", UUID.randomUUID().toString().replace("-", ""));
    }

    public ResourceOwner userCreateDraft(String username, String password) {
        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setEmail(username);
        resourceOwner.setPassword(password);
        return resourceOwner;
    }

    public ResourceOwner randomRegisterAnUser() {
        ResourceOwner random = randomCreateUserDraft();
        if (registerRealUser) {
            ResponseEntity<DefaultOAuth2AccessToken> defaultOAuth2AccessTokenResponseEntity = registerAnUser(random);
        }
        return random;
    }

    public String getRegisterToken() {
        return getJwtClientCredential(CLIENT_ID_REGISTER_ID, EMPTY_CLIENT_SECRET);
    }

    public ResponseEntity<DefaultOAuth2AccessToken> registerAnUser(ResourceOwner user) {
        String registerToken = getRegisterToken();
        PendingResourceOwner pendingResourceOwner = new PendingResourceOwner();
        createPendingUser(user, registerToken, pendingResourceOwner);
        return enterActivationCode(user, registerToken, pendingResourceOwner);
    }

    public ResponseEntity<DefaultOAuth2AccessToken> enterActivationCode(ResourceOwner user, String registerToken, PendingResourceOwner pendingResourceOwner) {
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.setBearerAuth(registerToken);
        headers1.set("changeId", UUID.randomUUID().toString());
        pendingResourceOwner.setPassword(user.getPassword());
        pendingResourceOwner.setActivationCode("123456");
        HttpEntity<PendingResourceOwner> request1 = new HttpEntity<>(pendingResourceOwner, headers1);
        return restTemplate.exchange(helper.getAccessUrl("/users"), HttpMethod.POST, request1, DefaultOAuth2AccessToken.class);
    }

    public ResponseEntity<Void> createPendingUser(ResourceOwner user, String registerToken, PendingResourceOwner pendingResourceOwner) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(registerToken);
        headers.set("changeId", UUID.randomUUID().toString());
        pendingResourceOwner.setEmail(user.getEmail());
        HttpEntity<PendingResourceOwner> request = new HttpEntity<>(pendingResourceOwner, headers);
        return restTemplate.exchange(helper.getAccessUrl("/pending-users"), HttpMethod.POST, request, Void.class);
    }

    public String registerResourceOwnerThenLogin() {
        ResourceOwner randomResourceOwner = randomCreateUserDraft();
        if (registerRealUser)
            registerAnUser(randomResourceOwner);
        return getJwtForUser(randomResourceOwner.getEmail(), randomResourceOwner.getPassword());
    }

    public ResponseEntity<DefaultOAuth2AccessToken> getJwtPassword(String username, String userPwd) {
        return getJwtPasswordWithClient(CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET, username, userPwd);
    }

    public String getJwtForUser(String username, String userPwd) {
        if (registerRealUser) {
            return getJwtPasswordWithClient(CLIENT_ID_LOGIN_ID, EMPTY_CLIENT_SECRET, username, userPwd).getBody().getValue();
        } else {
            String substring = "MOCK" + username.substring(0, 8);
            String head = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Im1hbnl0cmVlLWlkIn0.";
            String body = "{\"uid\":\"" + substring + "\",\"aud\":[\"0C8HPGLXHMET\",\"0C8HPGF4GBUP\"],\"user_name\":null,\"scope\":[\"0P8HPG99R56P\"],\"exp\":1645762519,\"iat\":1645761319,\"projectId\":\"0P8HPG99R56P\",\"client_id\":\"0C8HQM52YN7K\"}";
            String footer = ".dtEFAb1DFuwsdxL9MLIJNCucg2DCfWW0_9vt1IQ6__hlMm2qwICOtIoLNWLsc8PdkTDY_DDyFPVFSmip1W0ulLYD28VlusrJgzizCdDePTsXvn9qpqSnaurljSK3BQZEdS84MET97po2XfTQYUXhbvbihTm1VPNwSF9BxdBuRC2E6EjMUTLmvbukOWN57_khwhd_uWH24uNSWIhrGy7QyVYjdUAHeyKbhuORlyPQzZbQgAM8dMKD5wtnoivdH9DvuemqkSjjVpCllJpLhjQfSh6mRNXXfAX5MHgCsxGtc9svvXnwrEQyFcU8KfcxGWmBm1SB9Pkd0BEKxEwKYhwNVA";
            return head + Base64.getEncoder().encodeToString(body.getBytes()) + footer;
        }
    }

    public ResponseEntity<DefaultOAuth2AccessToken> getJwtPasswordWithClient(String clientId, String clientSecret, String username, String userPwd) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE_PASSWORD);
        params.add("username", username);
        params.add("password", userPwd);
        params.add("scope", "not_used");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.exchange(PROXY_URL_TOKEN, HttpMethod.POST, request, DefaultOAuth2AccessToken.class);
    }

    public String getJwtClientCredential(String clientId, String clientSecret) {
        if (registerRealUser) {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", GRANT_TYPE_CLIENT_CREDENTIALS);
            params.add("scope", "not_used");
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(clientId, clientSecret);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            return restTemplate.exchange(URL, HttpMethod.POST, request, DefaultOAuth2AccessToken.class).getBody().getValue();
        } else {
            String head = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Im1hbnl0cmVlLWlkIn0.";
            String body = "{\"uid\":\"" + "MOCK" + clientId + "\",\"aud\":[\"0C8HPGLXHMET\",\"0C8HPGF4GBUP\"],\"user_name\":null,\"scope\":[\"0P8HPG99R56P\"],\"exp\":1645762519,\"iat\":1645761319,\"projectId\":\"0P8HPG99R56P\",\"client_id\":\"0C8HQM52YN7K\"}";
            String footer = ".dtEFAb1DFuwsdxL9MLIJNCucg2DCfWW0_9vt1IQ6__hlMm2qwICOtIoLNWLsc8PdkTDY_DDyFPVFSmip1W0ulLYD28VlusrJgzizCdDePTsXvn9qpqSnaurljSK3BQZEdS84MET97po2XfTQYUXhbvbihTm1VPNwSF9BxdBuRC2E6EjMUTLmvbukOWN57_khwhd_uWH24uNSWIhrGy7QyVYjdUAHeyKbhuORlyPQzZbQgAM8dMKD5wtnoivdH9DvuemqkSjjVpCllJpLhjQfSh6mRNXXfAX5MHgCsxGtc9svvXnwrEQyFcU8KfcxGWmBm1SB9Pkd0BEKxEwKYhwNVA";
            return head + Base64.getEncoder().encodeToString(body.getBytes()) + footer;
        }

    }

    public OrderDetail createOrderDetailForUser(String authToken) {
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> productsByQuery = readProductsByQuery();
        List<ProductCustomerSummaryPaginatedRepresentation.ProductSearchRepresentation> products = productsByQuery.getBody().getData();

        ProductCustomerSummaryPaginatedRepresentation.ProductSearchRepresentation selectedProduct = products.get(new Random().nextInt(products.size()));
        String productInfoUrl = helper.getMallUrl("/products/public/" + selectedProduct.getId());
        ResponseEntity<ProductDetailCustomRepresentation> productDetail = restTemplate.exchange(productInfoUrl, HttpMethod.GET, null, ProductDetailCustomRepresentation.class);
        while (productDetail.getBody().getSkus().stream().anyMatch(e -> e.getStorage().equals(0))) {
            ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> nextProductsByQuery = readProductsByQuery();

            ProductCustomerSummaryPaginatedRepresentation.ProductSearchRepresentation nextSelectedProduct = products.get(new Random().nextInt(nextProductsByQuery.getBody().getData().size()));
            String url3 = helper.getMallUrl("/products/public/" + nextSelectedProduct.getId());
            productDetail = restTemplate.exchange(url3, HttpMethod.GET, null, ProductDetailCustomRepresentation.class);
        }
        SnapshotProduct cartItem = selectProduct(productDetail.getBody());
        String cartUrl = helper.getUserProfileUrl("/cart/user");
        ResponseEntity<String> addCartResponse = restTemplate.exchange(cartUrl, HttpMethod.POST, getHttpRequestAsString(authToken, cartItem), String.class);

        ResponseEntity<SumTotalSnapshotProduct> exchange5 = restTemplate.exchange(cartUrl, HttpMethod.GET, getHttpRequest(authToken), SumTotalSnapshotProduct.class);

        OrderDetail orderDetail = new OrderDetail();
        SnapshotAddress snapshotAddress = new SnapshotAddress();
        BeanUtils.copyProperties(getRandomAddress(), snapshotAddress);
        orderDetail.setAddress(snapshotAddress);
        exchange5.getBody().getData().forEach(e -> {
            e.setCartId(e.getId());

        });
        orderDetail.setProductList(exchange5.getBody().getData());
        orderDetail.setPaymentType(PaymentType.WECHAT_PAY);
        BigDecimal reduce = orderDetail.getProductList().stream().map(e -> BigDecimal.valueOf(Double.parseDouble(e.getFinalPrice()))).reduce(BigDecimal.valueOf(0), BigDecimal::add);
        orderDetail.setPaymentAmt(reduce);
        return orderDetail;
    }

    public OrderDetail createBizOrderForUserAndProduct(String defaultUserToken, String productId) {
        ResponseEntity<ProductDetailCustomRepresentation> productDetailCustomRepresentationResponseEntity = readProductDetailById(productId);
        SnapshotProduct snapshotProduct = selectProduct(productDetailCustomRepresentationResponseEntity.getBody());
        String url2 = helper.getUserProfileUrl("/cart/user");
        restTemplate.exchange(url2, HttpMethod.POST, getHttpRequestAsString(defaultUserToken, snapshotProduct), String.class);

        ResponseEntity<SumTotalSnapshotProduct> exchange5 = restTemplate.exchange(url2, HttpMethod.GET, getHttpRequest(defaultUserToken), SumTotalSnapshotProduct.class);

        OrderDetail orderDetail = new OrderDetail();
        SnapshotAddress snapshotAddress = new SnapshotAddress();
        BeanUtils.copyProperties(getRandomAddress(), snapshotAddress);
        orderDetail.setAddress(snapshotAddress);
        orderDetail.setProductList(exchange5.getBody().getData());
        orderDetail.setPaymentType(PaymentType.WECHAT_PAY);
        BigDecimal reduce = orderDetail.getProductList().stream().map(e -> BigDecimal.valueOf(Double.parseDouble(e.getFinalPrice()))).reduce(BigDecimal.valueOf(0), BigDecimal::add);
        orderDetail.setPaymentAmt(reduce);
        return orderDetail;
    }


    public ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> readProductsByQuery() {
        String query = "query=attr:3T8BRPK17W81-服装$3T8BRPK17W80-女&page=num:0,size:20,by:lowestPrice,order:asc";
        return readProductsByQuery(query);
    }

    public ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> readProductsByQuery(String query) {
        String url = helper.getUserProfileUrl("/products/public?" + query);
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> exchange = restTemplate.exchange(url, HttpMethod.GET, null, ProductCustomerSummaryPaginatedRepresentation.class);
        return exchange;
    }

    public SnapshotProduct selectProduct(ProductDetailCustomRepresentation productDetail) {
        List<ProductOption> selectedOptions = productDetail.getSelectedOptions();
        List<String> priceVarCollection = new ArrayList<>();
        if (selectedOptions != null && selectedOptions.size() != 0) {
            // pick first option
            selectedOptions.forEach(productOption -> {
                OptionItem optionItem = productOption.options.stream().findFirst().get();
                productOption.setOptions(List.of(optionItem));
                priceVarCollection.add(optionItem.getPriceVar());
            });
        }
        SnapshotProduct snapshotProduct = new SnapshotProduct();
        snapshotProduct.setName(productDetail.getName());
        snapshotProduct.setProductId(productDetail.getId());
        snapshotProduct.setSelectedOptions(productDetail.getSelectedOptions());
        snapshotProduct.setImageUrlSmall(productDetail.getImageUrlSmall());

        BigDecimal calc = new BigDecimal(0);
        for (String priceVar : priceVarCollection) {
            if (priceVar.contains("+")) {
                double v = Double.parseDouble(priceVar.replace("+", ""));
                BigDecimal bigDecimal = BigDecimal.valueOf(v);
                calc = calc.add(bigDecimal);
            } else if (priceVar.contains("-")) {
                double v = Double.parseDouble(priceVar.replace("-", ""));
                BigDecimal bigDecimal = BigDecimal.valueOf(v);
                calc = calc.subtract(bigDecimal);

            } else if (priceVar.contains("*")) {
                double v = Double.parseDouble(priceVar.replace("*", ""));
                BigDecimal bigDecimal = BigDecimal.valueOf(v);
                calc = calc.multiply(bigDecimal);
            } else {
            }
        }
        // pick first option
        List<ProductSkuCustomerRepresentation> productSkuList = productDetail.getSkus();
        snapshotProduct.setFinalPrice(calc.add(productSkuList.get(0).getPrice()).toString());
        snapshotProduct.setAttributesSales(productSkuList.get(0).getAttributesSales());
        snapshotProduct.setSkuId(productSkuList.get(0).getSkuId());
        snapshotProduct.setAmount(1);
        return snapshotProduct;
    }

    public ResponseEntity<CategorySummaryCustomerRepresentation> getCatalogs() {
        String url = helper.getUserProfileUrl("/catalogs/public");
        return restTemplate.exchange(url, HttpMethod.GET, null, CategorySummaryCustomerRepresentation.class);
    }

    public CategorySummaryCardRepresentation getFixedCatalogFromList() {
        ResponseEntity<CategorySummaryCustomerRepresentation> categories = getCatalogs();
        List<CategorySummaryCardRepresentation> body = categories.getBody().getData();
        Assert.assertEquals("should get default catalog", "女装精品", body.get(0).getName());
        return body.get(0);
    }

    public ResponseEntity<String> createRandomProductDetail(Integer actualStorage) {
        return createRandomProductDetail(actualStorage, null);
    }

    public ResponseEntity<String> createRandomProductDetail(Integer actualStorage, Integer orderStorage) {
        CategorySummaryCardRepresentation catalogFromList = getFixedCatalogFromList();
        ProductDetail randomProduct = getRandomProduct(catalogFromList, actualStorage, orderStorage);
        CreateProductAdminCommand createProductAdminCommand = new CreateProductAdminCommand();
        BeanUtils.copyProperties(randomProduct, createProductAdminCommand);
        createProductAdminCommand.setSkus(randomProduct.getProductSkuList());
        createProductAdminCommand.setStartAt(new Date().getTime());
        String s1 = getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<CreateProductAdminCommand> request = new HttpEntity<>(createProductAdminCommand, headers);
        String url = helper.getUserProfileUrl("/products/admin");
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    public ResponseEntity<String> createRandomProductDetail(Set<String> attrKeys, Set<String> attrProd, BigDecimal price) {
        CategorySummaryCardRepresentation catalogFromList = getFixedCatalogFromList();
        ProductDetail randomProduct = getRandomProduct(catalogFromList, 10, 10, price);
        CreateProductAdminCommand createProductAdminCommand = new CreateProductAdminCommand();
        BeanUtils.copyProperties(randomProduct, createProductAdminCommand);
        createProductAdminCommand.setSkus(randomProduct.getProductSkuList());
        createProductAdminCommand.setStartAt(new Date().getTime());
        createProductAdminCommand.setAttributesKey(attrKeys);
        createProductAdminCommand.setAttributesProd(attrProd);
        String s1 = getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(s1);
        HttpEntity<CreateProductAdminCommand> request = new HttpEntity<>(createProductAdminCommand, headers);
        String url = helper.getUserProfileUrl("/products/admin");
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    public String getDefaultAdminToken() {
        return getJwtForUser(ACCOUNT_USERNAME_ADMIN, ACCOUNT_PASSWORD_ADMIN);
    }

    public HttpEntity getHttpRequest(String authorizeToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        headers.setBearerAuth(authorizeToken);
        return new HttpEntity<>(headers);
    }

    public HttpEntity getHttpRequestAsString(String authorizeToken, Object object) {
        String s = null;
        try {
            s = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        headers.setBearerAuth(authorizeToken);
        return new HttpEntity<>(s, headers);
    }

    public <T> HttpEntity<T> getHttpRequest(String authorizeToken, T object) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        headers.setBearerAuth(authorizeToken);
        return new HttpEntity<>(object, headers);
    }

    public Catalog generateRandomFrontendCatalog() {
        Catalog category = new Catalog();
        category.setName(UUID.randomUUID().toString().replace("-", ""));
        category.setCatalogType(CatalogType.FRONTEND);
        HashSet<String> strings = new HashSet<>();
        strings.add(TEST_TEST_VALUE);
        category.setAttributes(strings);
        return category;
    }

    public Address getRandomAddress() {
        Address address = new Address();
        address.setCity(UUID.randomUUID().toString().replace("-", ""));
        address.setCountry(UUID.randomUUID().toString().replace("-", ""));
        address.setFullName(UUID.randomUUID().toString().replace("-", ""));
        address.setLine1(UUID.randomUUID().toString().replace("-", ""));
        address.setLine2(UUID.randomUUID().toString().replace("-", ""));
        address.setPhoneNumber(UUID.randomUUID().toString().replace("-", ""));
        address.setPostalCode(UUID.randomUUID().toString().replace("-", ""));
        address.setProvince(UUID.randomUUID().toString().replace("-", ""));
        return address;
    }

    public ProductDetail getRandomProduct(CategorySummaryCardRepresentation catalog, Integer actualStorage, Integer orderStorage, BigDecimal price) {
        ProductDetail productDetail = new ProductDetail();
        productDetail.setImageUrlSmall("http://www.test.com/" + UUID.randomUUID().toString().replace("-", ""));
        HashSet<String> objects = new HashSet<>();
        objects.add(UUID.randomUUID().toString().replace("-", ""));
        objects.add(UUID.randomUUID().toString().replace("-", ""));
        productDetail.setSpecification(objects);
        productDetail.setName(UUID.randomUUID().toString().replace("-", ""));
        productDetail.setAttributesKey(catalog.getAttributes());
        productDetail.setStatus(ProductStatus.AVAILABLE);
        int i = new Random().nextInt(2000);
        ProductSku productSku = new ProductSku();
        if (price == null)
            productSku.setPrice(BigDecimal.valueOf(new Random().nextDouble()).abs());
        else
            productSku.setPrice(price);
        productSku.setAttributesSales(new HashSet<>(List.of(TEST_TEST_VALUE)));

        if (actualStorage == null) {
            productSku.setStorageActual(i + new Random().nextInt(1000));
        } else {
            productSku.setStorageActual(actualStorage);
        }
        if (orderStorage == null) {
            productSku.setStorageOrder(i);
        } else {
            productSku.setStorageOrder(orderStorage);
        }
        productDetail.setProductSkuList(new ArrayList<>(List.of(productSku)));
        return productDetail;
    }

    public ProductDetail getRandomProduct(CategorySummaryCardRepresentation catalog, Integer actualStorage, Integer orderStorage) {
        return getRandomProduct(catalog, actualStorage, orderStorage, null);
    }

    public ProductDetail getRandomProduct(CategorySummaryCardRepresentation catalog) {
        return getRandomProduct(catalog, null, null);
    }

    public ProductDetail getRandomProduct(CategorySummaryCardRepresentation catalog, Integer actualStorage) {
        return getRandomProduct(catalog, actualStorage, null);
    }


    public String getRandomStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public ResponseEntity<ProductDetailCustomRepresentation> readRandomProductDetail() {
        ResponseEntity<ProductCustomerSummaryPaginatedRepresentation> randomProducts = readProductsByQuery();
        ProductCustomerSummaryPaginatedRepresentation.ProductSearchRepresentation productSimple = randomProducts.getBody().getData().get(new Random().nextInt(randomProducts.getBody().getData().size()));
        return readProductDetailById(productSimple.getId());
    }

    public ResponseEntity<ProductDetailCustomRepresentation> readProductDetailById(String id) {
        String url = helper.getUserProfileUrl("/products/public/" + id);
        return restTemplate.exchange(url, HttpMethod.GET, null, ProductDetailCustomRepresentation.class);
    }

    public ResponseEntity<ProductDetailAdminRepresentation> readProductDetailByIdAdmin(String id) {
        String defaultAdminToken = getDefaultAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(defaultAdminToken);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        String url = helper.getUserProfileUrl("/products/admin/" + id);
        return restTemplate.exchange(url, HttpMethod.GET, request, ProductDetailAdminRepresentation.class);
    }
}
