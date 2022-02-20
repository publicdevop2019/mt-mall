import { Component, Input, OnInit } from '@angular/core';
import { IAddress } from 'src/app/modules/account/addresses/addresses.component';
import { ICartItem } from 'src/app/pages/cart/cart.component';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
export interface IOrder {
    id: string;
    paymentAmt:number;
    paid:boolean;
    orderState:string;
    productList: ICartItem[];
    address?: IAddress;
    paymentType?: string; // wechat pay or ali pay
    paymentLink?: string; // wechat pay or ali pay
}
@Component({
    selector: 'app-card-order',
    templateUrl: './card-order.component.html',
    styleUrls: ['./card-order.component.scss']
})
export class CardOrderComponent implements OnInit {
    @Input() order: IOrder;
    constructor(private httpSvc:HttpProxyService,private notif:SnackbarService) {}

    ngOnInit() {}
    deleteOrder(){
        this.httpSvc.deleteOrder(this.order).subscribe(()=>{
            this.notif.openSnackBar('CANCEL_REQUEST_RECEIVED')
        })
    }

}
