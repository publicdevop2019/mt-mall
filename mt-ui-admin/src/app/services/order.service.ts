import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IOrder } from '../clazz/validation/interfaze-common';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';

@Injectable({
  providedIn: 'root'
})
export class OrderService extends EntityCommonService<IOrder, IOrder> {
  entityRepo: string = environment.serverUri +'/profile-svc/orders/admin';
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
      super(httpProxy, interceptor,deviceSvc);
  }
  deleteVersionedById(id: string, changeId: string,version:number) {
    this.httpProxySvc.deleteVersionedEntityById(this.entityRepo, id, changeId,version).subscribe(next => {
        this.notify(!!next)
        this.deviceSvc.refreshSummary.next();
    })
};
}
