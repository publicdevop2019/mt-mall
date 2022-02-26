package com.mt.shop.domain.model.biz_order;

import com.mt.common.domain.model.restful.exception.AggregateOutdatedException;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Slf4j
public class Auditable implements Serializable {
    private String createdBy;
    private Date createdAt;
    private String modifiedBy;
    private Date modifiedAt;
    private boolean deleted = false;
    private String deletedBy;
    private Date deletedAt;
    private String restoredBy;
    private Date restoredAt;
    protected Integer version;

    public void checkVersion(Integer version) {
        if (getVersion() != null || version != null) {
            if ((getVersion() == null && version != null)
                    ||
                    (getVersion() != null && version == null)
                    ||
                    !getVersion().equals(version)
            ){
                log.debug("expected {} but got {}",getVersion(),version);
                throw new CommandExecutionException("version mismatch", new AggregateOutdatedException(), new AggregateOutdatedException());
            }
        }
    }

    public void validate(@NotNull ValidationNotificationHandler handler) {
    }
}
