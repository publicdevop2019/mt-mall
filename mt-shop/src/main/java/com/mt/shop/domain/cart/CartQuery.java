package com.mt.shop.domain.cart;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.shop.application.cart.command.CancelClearCartForCreateCommand;
import com.mt.shop.application.cart.command.CancelRestoreCartForInvalidEvent;
import com.mt.shop.application.cart.command.ClearCartForCreateEvent;
import com.mt.shop.application.cart.command.RestoreCartForInvalidEvent;
import com.mt.shop.domain.biz_order.UserThreadLocal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CartQuery extends QueryCriteria {
    public static final String CREATED_BY = "createdBy";
    public static final String ID = "id";
    @Setter(AccessLevel.PRIVATE)
    private String userId;
    @Setter(AccessLevel.PRIVATE)
    private Set<CartItemId> cartItemIds;
    private CartSort cartSort;

    public CartQuery(CartItemId cartItemId) {
        userId = UserThreadLocal.get();
        this.cartItemIds = Collections.singleton(cartItemId);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setCartSort(pageConfig);
        CartQueryValidator cartQueryValidator = new CartQueryValidator(this, new HttpValidationNotificationHandler());
        cartQueryValidator.validate();
    }

    public CartQuery() {
        userId = UserThreadLocal.get();
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setCartSort(pageConfig);
        CartQueryValidator cartQueryValidator = new CartQueryValidator(this, new HttpValidationNotificationHandler());
        cartQueryValidator.validate();
    }

    public CartQuery(CancelClearCartForCreateCommand deserialize) {
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setCartSort(pageConfig);
        setUserId(deserialize.getUserId());
        Set<String> strings = deserialize.getIdVersionMap().keySet();
        setCartItemIds(strings.stream().map(CartItemId::new).collect(Collectors.toSet()));
        CartQueryValidator cartQueryValidator = new CartQueryValidator(this, new HttpValidationNotificationHandler());
        cartQueryValidator.validate();
    }

    public CartQuery(RestoreCartForInvalidEvent event) {
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setCartSort(pageConfig);
        setUserId(event.getUserId());
        Set<String> strings = event.getIdVersionMap().keySet();
        setCartItemIds(strings.stream().map(CartItemId::new).collect(Collectors.toSet()));
        CartQueryValidator cartQueryValidator = new CartQueryValidator(this, new HttpValidationNotificationHandler());
        cartQueryValidator.validate();
    }

    public CartQuery(CancelRestoreCartForInvalidEvent event) {
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setCartSort(pageConfig);
        setUserId(event.getUserId());
        Set<String> strings = event.getIds();
        setCartItemIds(strings.stream().map(CartItemId::new).collect(Collectors.toSet()));
        CartQueryValidator cartQueryValidator = new CartQueryValidator(this, new HttpValidationNotificationHandler());
        cartQueryValidator.validate();
    }

    public CartQuery(String query) {
        setUserId(UserThreadLocal.get());
        updateQueryParam(query);//update after thread local when query has userId
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setCartSort(pageConfig);
        CartQueryValidator cartQueryValidator = new CartQueryValidator(this, new HttpValidationNotificationHandler());
        cartQueryValidator.validate();
    }

    public CartQuery(ClearCartForCreateEvent clearCartForCreateEvent) {
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setCartSort(pageConfig);
        setUserId(clearCartForCreateEvent.getUserId());
        setCartItemIds(clearCartForCreateEvent.getIds().stream().map(CartItemId::new).collect(Collectors.toSet()));
        CartQueryValidator cartQueryValidator = new CartQueryValidator(this, new HttpValidationNotificationHandler());
        cartQueryValidator.validate();
    }

    private void setCartSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase("id")) {
            this.cartSort = CartSort.byId(pageConfig.isSortOrderAsc());
        }
        Validator.notNull(this.cartSort);
    }

    private void updateQueryParam(String query) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(query, CREATED_BY, ID);
        Optional.ofNullable(stringStringMap.get(CREATED_BY)).ifPresent(e -> {
            userId = e;
        });
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            cartItemIds = Arrays.stream(e.split("\\.")).map(CartItemId::new).collect(Collectors.toSet());
        });
    }

    @Getter
    public static class CartSort {
        private final boolean isById = true;
        private final boolean isAsc;

        private CartSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static CartSort byId(boolean isAsc) {
            return new CartSort(isAsc);
        }
    }
}
