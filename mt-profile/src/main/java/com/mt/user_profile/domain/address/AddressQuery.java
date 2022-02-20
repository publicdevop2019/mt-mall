package com.mt.user_profile.domain.address;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.user_profile.domain.biz_order.UserThreadLocal;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class AddressQuery extends QueryCriteria {
    public static final String ID = "id";
    private final String userId;
    private boolean admin = false;
    private AddressSort addressSort;
    private Set<AddressId> addressIds;

    public AddressQuery(String queryParam, String pageParam, String skipCount, boolean isAdmin) {
        updateParam(queryParam);
        userId = UserThreadLocal.get();
        setPageConfig(PageConfig.limited(pageParam, 200));
        setQueryConfig(new QueryConfig(skipCount));
        setAddressSort(this.pageConfig);
        admin = isAdmin;
        AddressQueryValidator addressQueryValidator = new AddressQueryValidator(this, new HttpValidationNotificationHandler());
        addressQueryValidator.validate();
    }

    public AddressQuery(AddressId addressId, boolean isAdmin) {
        this.addressIds = Collections.singleton(addressId);
        userId = UserThreadLocal.get();
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setAddressSort(this.pageConfig);
        admin = isAdmin;
        AddressQueryValidator addressQueryValidator = new AddressQueryValidator(this, new HttpValidationNotificationHandler());
        addressQueryValidator.validate();
    }

    public AddressQuery(boolean isAdmin) {
        userId = UserThreadLocal.get();
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setAddressSort(this.pageConfig);
        admin = isAdmin;
        AddressQueryValidator addressQueryValidator = new AddressQueryValidator(this, new HttpValidationNotificationHandler());
        addressQueryValidator.validate();
    }

    private void updateParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam,ID);
        Optional.ofNullable(stringStringMap.get(ID)).ifPresent(e -> {
            addressIds = Arrays.stream(e.split("\\.")).map(AddressId::new).collect(Collectors.toSet());
        });
    }

    private void setAddressSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase(ID)) {
            this.addressSort = AddressSort.byId(pageConfig.isSortOrderAsc());
        }
        Validator.notNull(this.addressSort);
    }

    @Getter
    public static class AddressSort {
        private final boolean isById = true;
        private final boolean isAsc;

        public static AddressSort byId(boolean isAsc) {
            return new AddressSort(isAsc);
        }

        private AddressSort(boolean isAsc) {
            this.isAsc = isAsc;
        }
    }
}
