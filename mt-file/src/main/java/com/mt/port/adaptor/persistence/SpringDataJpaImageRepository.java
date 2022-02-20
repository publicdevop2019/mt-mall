package com.mt.port.adaptor.persistence;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.domain.image.Image;
import com.mt.domain.image.ImageQuery;
import com.mt.domain.image.ImageRepository;
import com.mt.domain.image.Image_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaImageRepository extends JpaRepository<Image, Long>, ImageRepository {

    default void add(Image address) {
        save(address);
    }

    default SumPagedRep<Image> imageOfQuery(ImageQuery query) {
        QueryUtility.QueryContext<Image> context = QueryUtility.prepareContext(Image.class, query);
        Optional.ofNullable(query.getIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId)
                .collect(Collectors.toSet()), Image_.IMAGE_ID, context));
        Order order = null;
        if (query.getSort().isById())
            order = QueryUtility.getDomainIdOrder(Image_.IMAGE_ID, context, query.getSort().isAsc());
        context.setOrder(order);
        return QueryUtility.pagedQuery(query, context);
    }

}
