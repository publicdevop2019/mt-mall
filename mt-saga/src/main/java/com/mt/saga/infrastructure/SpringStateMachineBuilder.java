package com.mt.saga.infrastructure;

import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.saga.appliction.order_state_machine.CommonOrderCommand;
import com.mt.saga.appliction.order_state_machine.OrderStateMachineApplicationService;
import com.mt.saga.domain.DomainRegistry;
import com.mt.saga.domain.model.order_state_machine.OrderStateMachineBuilder;
import com.mt.saga.domain.model.order_state_machine.event.*;
import com.mt.saga.domain.model.order_state_machine.exception.OrderPaymentValidationException;
import com.mt.saga.domain.model.order_state_machine.exception.OrderProductValidationException;
import com.mt.saga.domain.model.order_state_machine.exception.StateMachineCreationException;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderEvent;
import com.mt.saga.domain.model.order_state_machine.order.BizOrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.guard.Guard;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import java.util.EnumSet;

import static com.mt.saga.infrastructure.SpringStateMachineErrorHandler.ERROR_CLASS;

/**
 * each guard is an unit of work, roll back when failure happen
 */
@Configuration
@Slf4j
public class SpringStateMachineBuilder implements OrderStateMachineBuilder {

    public static final String ORDER_COMMAND_DETAIL = "BIZ_ORDER";

    @Autowired
    private OrderStateMachineApplicationService stateMachineApplicationService;

    @Autowired
    @Qualifier("CustomPool")
    private TaskExecutor customExecutor;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SpringStateMachineErrorHandler customStateMachineEventListener;

    public StateMachine<BizOrderStatus, BizOrderEvent> buildMachine(BizOrderStatus initialState) {
        StateMachineBuilder.Builder<BizOrderStatus, BizOrderEvent> builder = StateMachineBuilder.builder();
        try {
            builder.configureConfiguration()
                    .withConfiguration()
                    .autoStartup(true)
                    .listener(customStateMachineEventListener)
            ;
            builder.configureStates()
                    .withStates()
                    .initial(initialState)
                    .states(EnumSet.allOf(BizOrderStatus.class));
            builder.configureTransitions()
                    .withExternal()
                    .source(BizOrderStatus.DRAFT).target(BizOrderStatus.NOT_PAID_RESERVED)
                    .event(BizOrderEvent.NEW_ORDER)
                    .guard(validateOrderInfo()).action(createCreateOrderDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.NOT_PAID_RESERVED).target(BizOrderStatus.PAID_RESERVED)
                    .event(BizOrderEvent.CONFIRM_PAYMENT)
                    .guard(validatePaymentStatus())
                    .action(createConfirmOrderPaymentDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.NOT_PAID_RECYCLED).target(BizOrderStatus.PAID_RECYCLED)
                    .event(BizOrderEvent.CONFIRM_PAYMENT)
                    .guard(validatePaymentStatus())
                    .action(createConfirmOrderPaymentDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.PAID_RECYCLED).target(BizOrderStatus.PAID_RESERVED)
                    .event(BizOrderEvent.RESERVE)
                    .action(createReserveOrderDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.PAID_RECYCLED).target(BizOrderStatus.PAID_RESERVED)
                    .event(BizOrderEvent.CONFIRM_PAYMENT_SUCCESS)
                    .action(createReserveOrderDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.NOT_PAID_RECYCLED).target(BizOrderStatus.NOT_PAID_RESERVED)
                    .event(BizOrderEvent.RESERVE)
                    .action(createReserveOrderDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.PAID_RESERVED).target(BizOrderStatus.CONFIRMED)
                    .event(BizOrderEvent.CONFIRM_ORDER)
                    .action(createConcludeOrderTx())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.PAID_RESERVED).target(BizOrderStatus.CONFIRMED)
                    .event(BizOrderEvent.CONFIRM_PAYMENT_SUCCESS)
                    .action(createConcludeOrderTx())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.NOT_PAID_RESERVED).target(BizOrderStatus.NOT_PAID_RECYCLED)
                    .event(BizOrderEvent.RECYCLE_ORDER_STORAGE)
                    .action(createRecycleOrderDTX())
                    .and()
                    .withInternal()
                    .source(BizOrderStatus.NOT_PAID_RESERVED)
                    .event(BizOrderEvent.UPDATE_ADDRESS)
                    .action(updateOrderAddressDTX())
                    .and()
                    .withInternal()
                    .source(BizOrderStatus.NOT_PAID_RECYCLED)
                    .event(BizOrderEvent.UPDATE_ADDRESS)
                    .action(updateOrderAddressDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.NOT_PAID_RECYCLED).target(BizOrderStatus.CANCELING)
                    .event(BizOrderEvent.CANCEL_ORDER)
                    .action(invalidOrderDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.NOT_PAID_RESERVED).target(BizOrderStatus.CANCELING)
                    .event(BizOrderEvent.CANCEL_ORDER)
                    .action(invalidOrderDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.CONFIRMED).target(BizOrderStatus.CANCELING)
                    .event(BizOrderEvent.CANCEL_ORDER)
                    .action(invalidOrderDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.PAID_RESERVED).target(BizOrderStatus.CANCELING)
                    .event(BizOrderEvent.CANCEL_ORDER)
                    .action(invalidOrderDTX())
                    .and()
                    .withExternal()
                    .source(BizOrderStatus.PAID_RECYCLED).target(BizOrderStatus.CANCELING)
                    .event(BizOrderEvent.CANCEL_ORDER)
                    .action(invalidOrderDTX())
            ;
        } catch (Exception e) {
            throw new StateMachineCreationException(e);
        }
        return builder.build();
    }

    private Action<BizOrderStatus, BizOrderEvent> invalidOrderDTX() {
        return context -> {
            log.info("start of create invalid order dtx");
            log.info("order status is {}",context.getSource().getId());
            CommonOrderCommand command = context.getExtendedState().get(ORDER_COMMAND_DETAIL, CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CreateInvalidOrderDTXEvent(command));
            log.info("end of create invalid order dtx");
        };
    }

    private Action<BizOrderStatus, BizOrderEvent> updateOrderAddressDTX() {
        return context -> {
            log.info("start of create update address order dtx");
            log.info("order status is {}",context.getSource().getId());
            CommonOrderCommand command = context.getExtendedState().get(ORDER_COMMAND_DETAIL, CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CreateUpdateOrderAddressDTXEvent(command));
            log.info("end of create update address order dtx");
        };
    }

    private Action<BizOrderStatus, BizOrderEvent> createRecycleOrderDTX() {
        return context -> {
            log.info("start of create recycle order dtx");
            CommonOrderCommand command = context.getExtendedState().get(ORDER_COMMAND_DETAIL, CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CreateRecycleOrderDTXEvent(command));
            log.info("end of create recycle order dtx");
        };
    }

    private Action<BizOrderStatus, BizOrderEvent> createReserveOrderDTX() {
        return context -> {
            log.info("start of create reserve order dtx");
            CommonOrderCommand command = context.getExtendedState().get(ORDER_COMMAND_DETAIL, CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CreateReserveOrderDTXEvent(command, context.getTarget().getId()));
            log.info("end of create reserve order dtx");
        };
    }

    private Action<BizOrderStatus, BizOrderEvent> createConfirmOrderPaymentDTX() {
        return context -> {
            log.info("start of create confirm payment order dtx");
            CommonOrderCommand command = context.getExtendedState().get(ORDER_COMMAND_DETAIL, CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CreateConfirmOrderPaymentDTXEvent(command, context.getTarget().getId()));
            log.info("end of create confirm payment order dtx");
        };
    }

    private Action<BizOrderStatus, BizOrderEvent> createCreateOrderDTX() {
        return context -> {
            log.info("start of create create order dtx");
            CommonOrderCommand command = context.getExtendedState().get(ORDER_COMMAND_DETAIL, CommonOrderCommand.class);
            DomainEventPublisher.instance().publish(new CreateCreateOrderDTXEvent(command));
            log.info("end of create create order dtx");
        };
    }


    private Guard<BizOrderStatus, BizOrderEvent> validateOrderInfo() {
        return context -> {
            CommonOrderCommand command = context.getExtendedState().get(ORDER_COMMAND_DETAIL, CommonOrderCommand.class);
            log.info("validate order info of {}", command.getOrderId());
            try {
                return DomainRegistry.getProductService().validateOrderedProduct(command.getProductList());
            }catch (Exception ex){
                context.getStateMachine().setStateMachineError(new OrderProductValidationException(ex));
                return false;
            }
        };
    }


    private Action<BizOrderStatus, BizOrderEvent> createConcludeOrderTx() {
        return context -> {
            log.info("start of create conclude order dtx");
            CommonOrderCommand machineCommand = context.getExtendedState().get(ORDER_COMMAND_DETAIL, CommonOrderCommand.class);
            CreateConcludeOrderDTXEvent createConcludeOrderDTXEvent = new CreateConcludeOrderDTXEvent(machineCommand);
            DomainEventPublisher.instance().publish(createConcludeOrderDTXEvent);
            log.info("end of create conclude order dtx");
        };
    }

    private Guard<BizOrderStatus, BizOrderEvent> validatePaymentStatus() {
        return context -> {
            log.info("start of validate payment status");
            CommonOrderCommand bizOrder = context.getExtendedState().get(ORDER_COMMAND_DETAIL, CommonOrderCommand.class);
            try {
                return DomainRegistry.getPaymentService().confirmPaymentStatus(bizOrder.getOrderId());
            }catch (Exception ex){
                context.getStateMachine().setStateMachineError(new OrderPaymentValidationException(ex));
                return false;
            }
        };
    }


    @Override
    public void handle(CommonOrderCommand command) {
        log.debug("start of handing {} with id {}",command.getClass().getName(),command.getId());
        StateMachine<BizOrderStatus, BizOrderEvent> stateMachine = buildMachine(command.getOrderState());
        stateMachine.getExtendedState().getVariables().put(ORDER_COMMAND_DETAIL, command);
        stateMachine.sendEvent(command.getBizOrderEvent());
        log.debug("after state machine execution");
        if (stateMachine.hasStateMachineError()) {
            log.debug("error during execution");
            throw stateMachine.getExtendedState().get(ERROR_CLASS, RuntimeException.class);
        }
        log.debug("end of handing {} with id {}",command.getClass().getName(),command.getId());
    }

}
