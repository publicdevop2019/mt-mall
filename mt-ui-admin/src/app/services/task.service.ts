import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
export interface IBizTask {
    id: string,
    status: 'STARTED' | 'SUCCESS' | 'RESOLVED' | 'PENDING',
    changeId: string,
    createdAt: string,
    resolveReason?: string,
    orderId: string,
    statusMap: { [key: string]: 'STARTED' | 'SUCCESS' },
    emptyOptMap: { [key: string]: boolean },
    idMap: { [key: string]: string },
    version: number
    cancelable?: boolean
    retryable?: boolean
}
export type DTX_EVENT_TYPE = 'CreateOrderDtx' | 'ReserveOrderDtx'| 'RecycleOrderDtx' 
    | 'ConfirmOrderPaymentOrderDtx' 
    | 'ConcludeOrderDtx' | 'UpdateOrderAddressDtx' | 'InvalidOrderDtx'
@Injectable({
    providedIn: 'root'
})
export class TaskService extends EntityCommonService<IBizTask, IBizTask> {
    private SVC_NAME = '/saga-svc';
    private ENTITY_NAME = '/dtx';
    private dtxName: DTX_EVENT_TYPE = 'CreateOrderDtx';
    private queryPrefix = 'name:createOrderDtx';
    entityRepo: string = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
        super(httpProxy, interceptor, deviceSvc);
    }
    doCancel(id: string) {
        return this.httpProxySvc.cancelDtx(this.entityRepo, id)
    }
    doResolve(id: string, reason: string) {
        return this.httpProxySvc.resolveCancelDtx(this.entityRepo, id, reason)
    }
    updateDtxName(name: DTX_EVENT_TYPE) {
        this.dtxName = name;
        this.queryPrefix = "name:" + name;
    }
    getEventName() {
        return this.dtxName;
    }
    readEntityByQuery(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
        return this.httpProxySvc.readEntityByQuery<IBizTask>(this.entityRepo, num, size, query ? (this.queryPrefix + ',' + query) : this.queryPrefix, by, order, headers)
    };
    readCancelEntityByQuery(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
        return this.httpProxySvc.readEntityByQuery<IBizTask>(this.entityRepo, num, size, `name:${this.dtxName + '_cancel'}`, by, order, headers)
    };
}
