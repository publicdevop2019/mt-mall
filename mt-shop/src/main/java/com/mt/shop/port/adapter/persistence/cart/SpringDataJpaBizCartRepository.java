package com.mt.shop.port.adapter.persistence.cart;

import com.mt.common.domain.model.audit.Auditable_;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.clause.IsDeletedClause;
import com.mt.shop.domain.cart.BizCart;
import com.mt.shop.domain.cart.BizCartRepository;
import com.mt.shop.domain.cart.BizCart_;
import com.mt.shop.domain.cart.CartQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.Order;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface SpringDataJpaBizCartRepository extends JpaRepository<BizCart, Long>, BizCartRepository {

    default void add(BizCart bizCartItem) {
        save(bizCartItem);
    }

    default SumPagedRep<BizCart> cartItemOfQuery(CartQuery cartQuery) {
        QueryUtility.QueryContext<BizCart> queryContext = getBizCartQueryContext(cartQuery);
        return QueryUtility.pagedQuery(cartQuery, queryContext);
    }

    default SumPagedRep<BizCart> removedCartItemOfQuery(CartQuery cartQuery) {
        QueryUtility.QueryContext<BizCart> context = getBizCartQueryContext(cartQuery);
        //add soft delete is true
        context.getPredicates().add(new IsDeletedClause<BizCart>().getWhereClause(context.getCriteriaBuilder(), context.getRoot()));
        Optional.ofNullable(context.getCountPredicates()).ifPresent(e -> e.add(new IsDeletedClause<BizCart>().getWhereClause(context.getCriteriaBuilder(), context.getCountRoot())));
        return QueryUtility.nativePagedQuery(cartQuery, context);
    }

    private QueryUtility.QueryContext<BizCart> getBizCartQueryContext(CartQuery cartQuery) {
        QueryUtility.QueryContext<BizCart> queryContext = QueryUtility.prepareContext(BizCart.class, cartQuery);
        Optional.ofNullable(cartQuery.getUserId()).ifPresent(userId -> QueryUtility.addStringEqualPredicate(userId, Auditable_.CREATED_BY, queryContext));
        Optional.ofNullable(cartQuery.getCartItemIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream()
                .map(DomainId::getDomainId).collect(Collectors.toSet()), BizCart_.CART_ITEM_ID, queryContext));
        Order order = null;
        if (cartQuery.getCartSort().isById())
            order = QueryUtility.getDomainIdOrder(BizCart_.CART_ITEM_ID, queryContext, cartQuery.getCartSort().isAsc());
        queryContext.setOrder(order);
        return queryContext;
    }

    default SumPagedRep<BizCart> internalCartItemOfQuery(CartQuery cartQuery) {
        QueryUtility.QueryContext<BizCart> queryContext = getBizCartQueryContext(cartQuery);
        return QueryUtility.nativePagedQuery(cartQuery, queryContext);
    }

    default void remove(BizCart e) {
        e.setDeleted(true);
        e.setDeletedAt(Date.from(Instant.now()));
        save(e);
    }

    default void removeAll(Set<BizCart> allByQuery) {
        allByQuery.forEach((cart) -> {
            cart.setDeletedAt(Date.from(Instant.now()));
            cart.setDeleted(true);
        });
        saveAll(allByQuery);
    }

    default void restoreAll(Set<BizCart> allByQuery) {
        allByQuery.forEach(e -> e.setDeleted(false));
        saveAll(allByQuery);
    }
}
