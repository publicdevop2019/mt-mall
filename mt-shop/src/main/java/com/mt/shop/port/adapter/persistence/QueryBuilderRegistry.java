package com.mt.shop.port.adapter.persistence;

import com.mt.shop.port.adapter.persistence.catalog.SpringDataJpaCatalogRepository;
import com.mt.shop.port.adapter.persistence.filter.SpringDataJpaFilterRepository;
import com.mt.shop.port.adapter.persistence.meta.SpringDataJpaMetaRepository;
import com.mt.shop.port.adapter.persistence.product.SpringDataJpaProductRepository;
import com.mt.shop.port.adapter.persistence.sku.SpringDataJpaSkuRepository;
import com.mt.shop.port.adapter.persistence.tag.SpringDataJpaTagRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderRegistry {
    @Getter
    private static SpringDataJpaCatalogRepository.JpaCriteriaApiCatalogAdaptor catalogSelectQueryBuilder;
    @Getter
    private static SpringDataJpaFilterRepository.JpaCriteriaApiFilterAdaptor filterSelectQueryBuilder;
    @Getter
    private static SpringDataJpaSkuRepository.JpaCriteriaApiSkuAdapter skuSelectQueryBuilder;
    @Getter
    private static SpringDataJpaTagRepository.JpaCriteriaApiTagAdaptor tagSelectQueryBuilder;
    @Getter
    private static SpringDataJpaProductRepository.JpaCriteriaApiProductAdaptor productSelectQueryBuilder;
    @Getter
    private static SpringDataJpaProductRepository.ProductUpdateQueryBuilder productUpdateQueryBuilder;
    @Getter
    private static SpringDataJpaSkuRepository.SkuUpdateQueryBuilder skuUpdateQueryBuilder;
    @Getter
    private static SpringDataJpaMetaRepository.JpaCriteriaApiMetaAdaptor metaAdaptor;

    @Autowired
    public void setMetaAdaptor(SpringDataJpaMetaRepository.JpaCriteriaApiMetaAdaptor metaAdaptor) {
        QueryBuilderRegistry.metaAdaptor = metaAdaptor;
    }

    @Autowired
    public void setSkuUpdateQueryBuilder(SpringDataJpaSkuRepository.SkuUpdateQueryBuilder skuUpdateQueryBuilder) {
        QueryBuilderRegistry.skuUpdateQueryBuilder = skuUpdateQueryBuilder;
    }

    @Autowired
    public void setProductUpdateQueryBuilder(SpringDataJpaProductRepository.ProductUpdateQueryBuilder productUpdateQueryBuilder) {
        QueryBuilderRegistry.productUpdateQueryBuilder = productUpdateQueryBuilder;
    }

    @Autowired
    public void setProductSelectQueryBuilder(SpringDataJpaProductRepository.JpaCriteriaApiProductAdaptor productSelectQueryBuilder) {
        QueryBuilderRegistry.productSelectQueryBuilder = productSelectQueryBuilder;
    }

    @Autowired
    public void setTagSelectQueryBuilder(SpringDataJpaTagRepository.JpaCriteriaApiTagAdaptor tagSelectQueryBuilder) {
        QueryBuilderRegistry.tagSelectQueryBuilder = tagSelectQueryBuilder;
    }

    @Autowired
    public void setSkuSelectQueryBuilder(SpringDataJpaSkuRepository.JpaCriteriaApiSkuAdapter skuSelectQueryBuilder) {
        QueryBuilderRegistry.skuSelectQueryBuilder = skuSelectQueryBuilder;
    }

    @Autowired
    public void setCatalogSelectQueryBuilder(SpringDataJpaCatalogRepository.JpaCriteriaApiCatalogAdaptor catalogSelectQueryBuilder) {
        QueryBuilderRegistry.catalogSelectQueryBuilder = catalogSelectQueryBuilder;
    }

    @Autowired
    public void setFilterSelectQueryBuilder(SpringDataJpaFilterRepository.JpaCriteriaApiFilterAdaptor filterSelectQueryBuilder) {
        QueryBuilderRegistry.filterSelectQueryBuilder = filterSelectQueryBuilder;
    }
}
