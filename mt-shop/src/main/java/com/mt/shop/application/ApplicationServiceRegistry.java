package com.mt.shop.application;

import com.mt.common.domain.model.idempotent.IdempotentService;
import com.mt.shop.application.address.AddressApplicationService;
import com.mt.shop.application.biz_order.BizOrderApplicationService;
import com.mt.shop.application.cart.CartApplicationService;
import com.mt.shop.application.catalog.CatalogApplicationService;
import com.mt.shop.application.filter.FilterApplicationService;
import com.mt.shop.application.image.ImageApplicationService;
import com.mt.shop.application.notification.NotificationApplicationService;
import com.mt.shop.application.payment.PaymentApplicationService;
import com.mt.shop.application.product.ProductApplicationService;
import com.mt.shop.application.sku.SkuApplicationService;
import com.mt.shop.application.tag.TagApplicationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceRegistry {
    @Getter
    private static CatalogApplicationService catalogApplicationService;
    @Getter
    private static IdempotentService idempotentWrapper;
    @Getter
    private static FilterApplicationService filterApplicationService;
    @Getter
    private static TagApplicationService tagApplicationService;
    @Getter
    private static SkuApplicationService skuApplicationService;
    @Getter
    private static ProductApplicationService productApplicationService;
    @Getter
    private static MetaApplicationService metaApplicationService;
    @Getter
    private static ImageApplicationService imageApplicationService;
    @Getter
    private static PaymentApplicationService paymentApplicationService;
    @Getter
    private static BizOrderApplicationService bizOrderApplicationService;
    @Getter
    private static AddressApplicationService addressApplicationService;
    @Getter
    private static CartApplicationService cartApplicationService;
    @Getter
    private static NotificationApplicationService notificationApplicationService;

    @Autowired
    public void setNotificationApplicationService(NotificationApplicationService notificationApplicationService) {
        ApplicationServiceRegistry.notificationApplicationService = notificationApplicationService;
    }

    @Autowired
    private void setCartApplicationService(CartApplicationService cartApplicationService) {
        ApplicationServiceRegistry.cartApplicationService = cartApplicationService;
    }

    @Autowired
    private void setBizOrderApplicationService(BizOrderApplicationService bizOrderApplicationService) {
        ApplicationServiceRegistry.bizOrderApplicationService = bizOrderApplicationService;
    }


    @Autowired
    private void setAddressApplicationService(AddressApplicationService addressApplicationService) {
        ApplicationServiceRegistry.addressApplicationService = addressApplicationService;
    }

    @Autowired
    private void setPaymentApplicationService(PaymentApplicationService paymentApplicationService) {
        ApplicationServiceRegistry.paymentApplicationService = paymentApplicationService;
    }

    @Autowired
    private void setImageApplicationService(ImageApplicationService imageApplicationService) {
        ApplicationServiceRegistry.imageApplicationService = imageApplicationService;
    }

    @Autowired
    public void setMetaApplicationService(MetaApplicationService metaApplicationService) {
        ApplicationServiceRegistry.metaApplicationService = metaApplicationService;
    }

    @Autowired
    public void setProductApplicationService(ProductApplicationService productApplicationService) {
        ApplicationServiceRegistry.productApplicationService = productApplicationService;
    }

    @Autowired
    public void setTagApplicationService(TagApplicationService tagApplicationService) {
        ApplicationServiceRegistry.tagApplicationService = tagApplicationService;
    }

    @Autowired
    public void setSkuApplicationService(SkuApplicationService skuApplicationService) {
        ApplicationServiceRegistry.skuApplicationService = skuApplicationService;
    }

    @Autowired
    public void setCatalogApplicationService(CatalogApplicationService catalogApplicationService) {
        ApplicationServiceRegistry.catalogApplicationService = catalogApplicationService;
    }

    @Autowired
    public void setFilterApplicationService(FilterApplicationService filterApplicationService) {
        ApplicationServiceRegistry.filterApplicationService = filterApplicationService;
    }

    @Autowired
    public void setApplicationServiceIdempotentWrapper(IdempotentService idempotentWrapper) {
        ApplicationServiceRegistry.idempotentWrapper = idempotentWrapper;
    }
}
