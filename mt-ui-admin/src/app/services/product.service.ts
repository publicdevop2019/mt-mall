import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IProductSimple, IProductDetail } from '../clazz/validation/aggregate/product/interfaze-product';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class ProductService extends EntityCommonService<IProductSimple, IProductDetail>{
  entityRepo: string = environment.serverUri  +'/product-svc/products/admin';
  constructor(private httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  updateProdStatus(id: string, status: 'AVAILABLE' | 'UNAVAILABLE', changeId: string) {
    this.httpProxy.updateProductStatus(id, status, changeId).subscribe(result => {
      this.notify(result)
      this.deviceSvc.refreshSummary.next()
    })
  }
  batchUpdateProdStatus(ids: string[], status: 'AVAILABLE' | 'UNAVAILABLE', changeId: string) {
    this.httpProxy.batchUpdateProductStatus(ids, status, changeId).subscribe(result => {
      this.notify(result)
      this.deviceSvc.refreshSummary.next()
    })
  }
}
