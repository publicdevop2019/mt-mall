import { Component, OnInit } from '@angular/core';
import { OrderService } from 'src/app/services/order.service';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-order-complete',
    templateUrl: './order-complete.component.html',
    styleUrls: ['./order-complete.component.scss']
})
export class OrderCompleteComponent implements OnInit {
    constructor(public orderSvc: OrderService, private titleSvc: Title,private trans:TranslateService) {
        this.trans.get(['ORDER_COMPLETE','DOC_TITLE']).subscribe(next=>{
            this.titleSvc.setTitle(Object.values(next).join(' '))
          })
    }

    ngOnInit() { }
}
