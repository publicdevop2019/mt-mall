package com.mt.domain.image;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;

public class ImageQuery extends QueryCriteria {
    @Getter
    private final Set<ImageId> ids;
    @Getter
    private final Sort sort;

    public ImageQuery(ImageId id) {
        this.ids = Collections.singleton(id);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        this.sort = Sort.byId(true);
    }

    @Getter
    public static class Sort {
        private final boolean isById = true;
        private final boolean isAsc;

        private Sort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static Sort byId(boolean isAsc) {
            return new Sort(isAsc);
        }
    }
}
