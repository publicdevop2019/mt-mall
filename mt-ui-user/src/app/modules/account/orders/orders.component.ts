import { Component, OnInit } from '@angular/core';
import { IOrder } from 'src/app/modules/account/card-order/card-order.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-orders',
    templateUrl: './orders.component.html',
    styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit {
    public orderTotal: IOrder[];
    public paiedOrders: IOrder[];
    public unpaiedOrders: IOrder[];
    constructor(public httpProxy: HttpProxyService, private titleSvc: Title,private trans:TranslateService) {
        this.trans.get(['ORDERS','ACCOUNT','DOC_TITLE']).subscribe(next1=>{
            this.titleSvc.setTitle(Object.values(next1).join(' '))
          })
        this.httpProxy.getOrders().subscribe(next => {
            this.orderTotal = next.data;
            this.paiedOrders = this.orderTotal.filter(e => e.paid);
            this.unpaiedOrders = this.orderTotal.filter(e => !e.paid);
        });
    }

    ngOnInit() { }
}
