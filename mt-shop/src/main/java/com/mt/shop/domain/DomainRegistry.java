package com.mt.shop.domain;

import com.mt.shop.domain.address.AddressRepository;
import com.mt.shop.domain.address.AddressValidationService;
import com.mt.shop.domain.biz_order.BizOderSummaryRepository;
import com.mt.shop.domain.biz_order.BizOrderService;
import com.mt.shop.domain.cart.BizCartRepository;
import com.mt.shop.domain.cart.BizCartValidationService;
import com.mt.shop.domain.model.catalog.CatalogRepository;
import com.mt.shop.domain.model.catalog.CatalogService;
import com.mt.shop.domain.model.catalog.CatalogValidationService;
import com.mt.shop.domain.model.filter.FilterRepository;
import com.mt.shop.domain.model.filter.FilterService;
import com.mt.shop.domain.model.filter.FilterValidationService;
import com.mt.shop.domain.model.image.ImageRepository;
import com.mt.shop.domain.model.meta.MetaRepository;
import com.mt.shop.domain.model.meta.MetaService;
import com.mt.shop.domain.model.payment.PaymentRepository;
import com.mt.shop.domain.model.payment.ThirdPartyPaymentService;
import com.mt.shop.domain.model.product.ProductRepository;
import com.mt.shop.domain.model.product.ProductService;
import com.mt.shop.domain.model.product.ProductTagRepository;
import com.mt.shop.domain.model.product.ProductValidationService;
import com.mt.shop.domain.model.sku.SkuRepository;
import com.mt.shop.domain.model.sku.SkuService;
import com.mt.shop.domain.model.tag.TagRepository;
import com.mt.shop.domain.model.tag.TagService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    @Getter
    private static CatalogRepository catalogRepository;
    @Getter
    private static CatalogService catalogService;
    @Getter
    private static FilterRepository filterRepository;
    @Getter
    private static FilterService filterService;
    @Getter
    private static TagService tagService;
    @Getter
    private static TagRepository tagRepository;
    @Getter
    private static SkuService skuService;
    @Getter
    private static SkuRepository skuRepository;
    @Getter
    private static ProductRepository productRepository;
    @Getter
    private static ProductService productService;
    @Getter
    private static ProductTagRepository productTagRepository;
    @Getter
    private static CatalogValidationService catalogValidationService;
    @Getter
    private static FilterValidationService filterValidationService;
    @Getter
    private static ProductValidationService productValidationService;
    @Getter
    private static MetaRepository metaRepository;
    @Getter
    private static MetaService metaService;
    @Getter
    private static ImageRepository imageRepository;
    @Getter
    private static PaymentRepository paymentRepository;
    @Getter
    private static ThirdPartyPaymentService weChatPayService;
    @Getter
    private static BizOderSummaryRepository bizOderSummaryRepository;
    @Getter
    private static BizOrderService bizOrderService;
    @Getter
    private static AddressRepository addressRepository;
    @Getter
    private static BizCartRepository bizCartRepository;
    @Getter
    private static AddressValidationService addressValidationService;
    @Getter
    private static BizCartValidationService bizCartValidationService;

    @Autowired
    private void setBizCartRepository(BizCartRepository bizCartRepository) {
        DomainRegistry.bizCartRepository = bizCartRepository;
    }

    @Autowired
    private void setBizCartValidationService(BizCartValidationService bizCartValidationService) {
        DomainRegistry.bizCartValidationService = bizCartValidationService;
    }

    @Autowired
    private void setAddressValidationService(AddressValidationService addressValidationService) {
        DomainRegistry.addressValidationService = addressValidationService;
    }

    @Autowired
    private void setAddressRepository(AddressRepository addressRepository) {
        DomainRegistry.addressRepository = addressRepository;
    }

    @Autowired
    private void setBizOrderService(BizOrderService bizOrderService) {
        DomainRegistry.bizOrderService = bizOrderService;
    }

    @Autowired
    private void setBizOderSummaryRepository(BizOderSummaryRepository bizOrderRepository) {
        DomainRegistry.bizOderSummaryRepository = bizOrderRepository;
    }

    @Autowired
    private void setPaymentRepository(PaymentRepository paymentRepository) {
        DomainRegistry.paymentRepository = paymentRepository;
    }

    @Autowired
    private void setWeChatPayService(ThirdPartyPaymentService weChatPayService) {
        DomainRegistry.weChatPayService = weChatPayService;
    }

    @Autowired
    private void setImageRepository(ImageRepository imageRepository) {
        DomainRegistry.imageRepository = imageRepository;
    }

    @Autowired
    private void setCatalogValidationService(FilterValidationService catalogValidationService) {
        DomainRegistry.filterValidationService = catalogValidationService;
    }

    @Autowired
    private void setMetaService(MetaService metaService) {
        DomainRegistry.metaService = metaService;
    }

    @Autowired
    private void setProductValidationService(ProductValidationService productValidationService) {
        DomainRegistry.productValidationService = productValidationService;
    }

    @Autowired
    private void setProductRepository(ProductRepository productRepository) {
        DomainRegistry.productRepository = productRepository;
    }

    @Autowired
    private void setTagValidationService(CatalogValidationService tagValidationService) {
        DomainRegistry.catalogValidationService = tagValidationService;
    }

    @Autowired
    private void setProductTagRepository(ProductTagRepository productTagRepository) {
        DomainRegistry.productTagRepository = productTagRepository;
    }

    @Autowired
    private void setProductService(ProductService productService) {
        DomainRegistry.productService = productService;
    }

    @Autowired
    private void setCatalogRepository(CatalogRepository catalogRepository) {
        DomainRegistry.catalogRepository = catalogRepository;
    }

    @Autowired
    private void setSkuRepository(SkuRepository skuRepository) {
        DomainRegistry.skuRepository = skuRepository;
    }

    @Autowired
    private void setSkuService(SkuService skuService) {
        DomainRegistry.skuService = skuService;
    }

    @Autowired
    private void setFilterService(FilterService filterService) {
        DomainRegistry.filterService = filterService;
    }

    @Autowired
    private void setTagService(TagService tagService) {
        DomainRegistry.tagService = tagService;
    }

    @Autowired
    private void setTagRepository(TagRepository tagRepository) {
        DomainRegistry.tagRepository = tagRepository;
    }

    @Autowired
    private void setFilterRepository(FilterRepository filterRepository) {
        DomainRegistry.filterRepository = filterRepository;
    }

    @Autowired
    private void setMetaRepository(MetaRepository metaRepository) {
        DomainRegistry.metaRepository = metaRepository;
    }

    @Autowired
    private void setCatalogService(CatalogService catalogService) {
        DomainRegistry.catalogService = catalogService;
    }
}
