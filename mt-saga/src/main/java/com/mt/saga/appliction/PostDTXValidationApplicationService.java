package com.mt.saga.appliction;

import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTX;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.CancelConcludeOrderDTXQuery;
import com.mt.saga.domain.model.cancel_conclude_order_dtx.event.CancelConcludeOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.CancelConfirmOrderPaymentDTXQuery;
import com.mt.saga.domain.model.cancel_confirm_order_payment_dtx.event.CancelConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTX;
import com.mt.saga.domain.model.cancel_create_order_dtx.CancelCreateOrderDTXQuery;
import com.mt.saga.domain.model.cancel_create_order_dtx.event.CancelCreateOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTX;
import com.mt.saga.domain.model.cancel_invalid_order.CancelInvalidOrderDTXQuery;
import com.mt.saga.domain.model.cancel_invalid_order.event.CancelInvalidOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTX;
import com.mt.saga.domain.model.cancel_recycle_dtx.CancelRecycleOrderDTXQuery;
import com.mt.saga.domain.model.cancel_recycle_dtx.event.CancelRecycleOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTX;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.CancelReserveOrderDTXQuery;
import com.mt.saga.domain.model.cancel_reserve_order_dtx.event.CancelReserveOrderDTXSuccessEvent;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTX;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.CancelUpdateOrderAddressDTXQuery;
import com.mt.saga.domain.model.cancel_update_order_address_dtx.event.CancelUpdateOrderAddressDTXSuccessEvent;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTX;
import com.mt.saga.domain.model.conclude_order_dtx.ConcludeOrderDTXQuery;
import com.mt.saga.domain.model.conclude_order_dtx.event.ConcludeOrderDTXSuccessEvent;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTX;
import com.mt.saga.domain.model.confirm_order_payment_dtx.ConfirmOrderPaymentDTXQuery;
import com.mt.saga.domain.model.confirm_order_payment_dtx.event.ConfirmOrderPaymentDTXSuccessEvent;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTX;
import com.mt.saga.domain.model.create_order_dtx.CreateOrderDTXQuery;
import com.mt.saga.domain.model.create_order_dtx.event.CreateOrderDTXSuccessEvent;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTX;
import com.mt.saga.domain.model.invalid_order.InvalidOrderDTXQuery;
import com.mt.saga.domain.model.invalid_order.event.InvalidOrderDTXSuccessEvent;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTX;
import com.mt.saga.domain.model.recycle_order_dtx.RecycleOrderDTXQuery;
import com.mt.saga.domain.model.recycle_order_dtx.event.RecycleOrderDTXSuccessEvent;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTX;
import com.mt.saga.domain.model.reserve_order_dtx.ReserveOrderDTXQuery;
import com.mt.saga.domain.model.reserve_order_dtx.event.ReserveOrderDTXSuccessEvent;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTX;
import com.mt.saga.domain.model.update_order_address_dtx.UpdateOrderAddressDTXQuery;
import com.mt.saga.domain.model.update_order_address_dtx.event.UpdateOrderAddressDTXSuccessEvent;
import com.mt.saga.infrastructure.AppConstant;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostDTXValidationApplicationService {
    public void handle(CancelConcludeOrderDTXSuccessEvent deserialize) {
        handleConcludeRelatedDtx(deserialize.getChangeId());
    }

    public void handle(CancelConfirmOrderPaymentDTXSuccessEvent deserialize) {
        handleConfirmPaymentRelatedDtx(deserialize.getChangeId());
    }

    public void handle(CancelCreateOrderDTXSuccessEvent deserialize) {
        handleCreateRelatedDtx(deserialize.getChangeId());
    }

    public void handle(CancelRecycleOrderDTXSuccessEvent deserialize) {
        handleRecycleRelatedDtx(deserialize.getChangeId());
    }

    public void handle(CancelReserveOrderDTXSuccessEvent deserialize) {
        handleReserveRelatedDtx(deserialize.getChangeId());
    }

    public void handle(ConcludeOrderDTXSuccessEvent deserialize) {
        handleConcludeRelatedDtx(deserialize.getChangeId());
    }

    public void handle(ConfirmOrderPaymentDTXSuccessEvent deserialize) {
        handleConfirmPaymentRelatedDtx(deserialize.getChangeId());
    }

    public void handle(CreateOrderDTXSuccessEvent deserialize) {
        handleCreateRelatedDtx(deserialize.getChangeId());
    }

    public void handle(RecycleOrderDTXSuccessEvent deserialize) {
        handleRecycleRelatedDtx(deserialize.getChangeId());
    }

    public void handle(ReserveOrderDTXSuccessEvent deserialize) {
        handleReserveRelatedDtx(deserialize.getChangeId());
    }

    private void handleReserveRelatedDtx(String changeId) {
        String[] changeIds = getChangeIds(changeId);
        Optional<ReserveOrderDTX> first = DomainRegistry.getReserveOrderDTXRepository().query(new ReserveOrderDTXQuery(changeIds[0])).findFirst();
        Optional<CancelReserveOrderDTX> first1 = DomainRegistry.getCancelReserveOrderDTXRepository().query(new CancelReserveOrderDTXQuery(changeIds[1])).findFirst();
        if (first.isPresent() && first1.isPresent())
            DomainRegistry.getPostDtxValidationService().validate(first.get(), first1.get());

    }

    private void handleRecycleRelatedDtx(String changeId) {
        String[] changeIds = getChangeIds(changeId);
        Optional<RecycleOrderDTX> first = DomainRegistry.getRecycleOrderDTXRepository().query(new RecycleOrderDTXQuery(changeIds[0])).findFirst();
        Optional<CancelRecycleOrderDTX> first1 = DomainRegistry.getCancelRecycleOrderDTXRepository().query(new CancelRecycleOrderDTXQuery(changeIds[1])).findFirst();
        if (first.isPresent() && first1.isPresent())
            DomainRegistry.getPostDtxValidationService().validate(first.get(), first1.get());
    }

    private void handleCreateRelatedDtx(String changeId) {
        String[] changeIds = getChangeIds(changeId);
        Optional<CreateOrderDTX> first = DomainRegistry.getCreateOrderDTXRepository().query(new CreateOrderDTXQuery(changeIds[0])).findFirst();
        Optional<CancelCreateOrderDTX> first1 = DomainRegistry.getCancelCreateOrderDTXRepository().query(new CancelCreateOrderDTXQuery(changeIds[1])).findFirst();
        if (first.isPresent() && first1.isPresent())
            DomainRegistry.getPostDtxValidationService().validate(first.get(), first1.get());
    }

    private void handleConcludeRelatedDtx(String changeId) {
        String[] changeIds = getChangeIds(changeId);
        Optional<ConcludeOrderDTX> first = DomainRegistry.getConcludeOrderDTXRepository().query(new ConcludeOrderDTXQuery(changeIds[0])).findFirst();
        Optional<CancelConcludeOrderDTX> first1 = DomainRegistry.getCancelConcludeOrderDTXRepository().query(new CancelConcludeOrderDTXQuery(changeIds[1])).findFirst();
        if (first.isPresent() && first1.isPresent())
            DomainRegistry.getPostDtxValidationService().validate(first.get(), first1.get());
    }
    private void handleConfirmPaymentRelatedDtx(String changeId) {
        String[] changeIds = getChangeIds(changeId);
        Optional<ConfirmOrderPaymentDTX> first = DomainRegistry.getConfirmOrderPaymentDTXRepository().query(new ConfirmOrderPaymentDTXQuery(changeIds[0])).findFirst();
        Optional<CancelConfirmOrderPaymentDTX> first1 = DomainRegistry.getCancelConfirmOrderPaymentDTXRepository().query(new CancelConfirmOrderPaymentDTXQuery(changeIds[1])).findFirst();
        if (first.isPresent() && first1.isPresent())
            DomainRegistry.getPostDtxValidationService().validate(first.get(), first1.get());
    }
    private void handleUpdateAddressRelatedDtx(String changeId) {
        String[] changeIds = getChangeIds(changeId);
        Optional<UpdateOrderAddressDTX> first = DomainRegistry.getUpdateOrderAddressDTXRepository().query(new UpdateOrderAddressDTXQuery(changeIds[0])).findFirst();
        Optional<CancelUpdateOrderAddressDTX> first1 = DomainRegistry.getCancelUpdateOrderAddressDTXRepository().query(new CancelUpdateOrderAddressDTXQuery(changeIds[1])).findFirst();
        if (first.isPresent() && first1.isPresent())
            DomainRegistry.getPostDtxValidationService().validate(first.get(), first1.get());
    }
    private void handleInvalidOrderRelatedDtx(String changeId) {
        String[] changeIds = getChangeIds(changeId);
        Optional<InvalidOrderDTX> first = DomainRegistry.getInvalidOrderDTXRepository().query(new InvalidOrderDTXQuery(changeIds[0])).findFirst();
        Optional<CancelInvalidOrderDTX> first1 = DomainRegistry.getCancelInvalidOrderDTXRepository().query(new CancelInvalidOrderDTXQuery(changeIds[1])).findFirst();
        if (first.isPresent() && first1.isPresent())
            DomainRegistry.getPostDtxValidationService().validate(first.get(), first1.get());
    }

    private String[] getChangeIds(String changeId){
        String var0;
        String var1;
        if (changeId.contains(AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX)) {
            var0 = changeId.replace(AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX, "");
            var1 = changeId;
        } else {
            var0 = changeId;
            var1 = changeId + AppConstant.APP_CHANGE_ID_CANCEL_SUFFIX;
        }
        return new String[]{var0, var1};
    }

    public void handle(UpdateOrderAddressDTXSuccessEvent deserialize) {
        handleUpdateAddressRelatedDtx(deserialize.getChangeId());
    }

    public void handle(CancelUpdateOrderAddressDTXSuccessEvent deserialize) {
        handleUpdateAddressRelatedDtx(deserialize.getChangeId());
    }

    public void handle(InvalidOrderDTXSuccessEvent deserialize) {
        handleInvalidOrderRelatedDtx(deserialize.getChangeId());
    }

    public void handle(CancelInvalidOrderDTXSuccessEvent deserialize) {
        handleInvalidOrderRelatedDtx(deserialize.getChangeId());

    }
}
