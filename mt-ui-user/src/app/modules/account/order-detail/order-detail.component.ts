import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { ActivatedRoute, Router } from '@angular/router';
import { of, Observable } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { notNullAndUndefined } from 'src/app/classes/utility';
import { IOrder } from 'src/app/modules/account/card-order/card-order.component';
import { CartService } from 'src/app/services/cart.service';
import { OrderService } from 'src/app/services/order.service';
import { BottomSheetAddressPickerComponent } from '../bottom-sheet-address-picker/bottom-sheet-address-picker.component';
import { BottomSheetPaymentPickerComponent } from '../bottom-sheet-payment-picker/bottom-sheet-payment-picker.component';
import { Title } from '@angular/platform-browser';
import { IAddress } from '../addresses/addresses.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-order-detail',
    templateUrl: './order-detail.component.html',
    styleUrls: ['./order-detail.component.scss']
})
export class OrderDetailComponent implements OnInit ,OnDestroy{
    public editable = false;
    public newOrder = false;
    public updatingAddress = false;
    public systemError: boolean = false;
    private count: number = 1;
    private pulls:any[]=[];
    constructor(
        private cartSvc: CartService,
        private bottomSheet: MatBottomSheet,
        public orderSvc: OrderService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private titleSvc: Title,
        private trans:TranslateService
    ) {
        this.orderSvc.order = {} as IOrder;
        this.activatedRoute.paramMap
            .pipe(
                switchMap(next => {
                    if (!notNullAndUndefined(next.get('orderId'))) {
                        /** create a new order */
                        this.newOrder = true;
                        this.trans.get(['NEW_ORDERS','ACCOUNT','DOC_TITLE']).subscribe(next=>{
                            this.titleSvc.setTitle(Object.values(next).join(' '))
                          })
                        return of({
                            productList: this.cartSvc.cart,
                            address: this.orderSvc.order.address,
                            paymentType: this.orderSvc.order.paymentType,
                            orderState: 'DRAFT',
                            id: this.orderSvc.order.id,
                        } as IOrder);
                    } else {
                        /** read an existing paid or unpaid order */
                        this.trans.get(['ORDERS_DETAIL','ACCOUNT','DOC_TITLE']).subscribe(next1=>{
                            this.titleSvc.setTitle([next.get('orderId'),...Object.values(next1)].join(' '))
                          })
                        return this.orderSvc.httpProxy.getOrderById(
                            next.get('orderId')
                        );
                    }
                })
            )
            .subscribe(next => {
                this.orderSvc.order = next;
                if (this.orderSvc.order.orderState.indexOf('DRAFT') > -1) {
                    this.editable = true;
                    this.orderSvc.updateAddress.pipe(take(1)).subscribe(() => {
                        this.updatingAddress = true;
                        console.dir('set interval')
                        const pull = setInterval(() => {
                             console.dir('inside interval')
                            this.orderSvc.httpProxy.getOrderById(this.orderSvc.order.id+ "?t=" + new Date().getTime()).subscribe(next => {
                                this.count++;
                                if (next && next.address && this.addressIsDifferent(next.address, this.orderSvc.order.address)) {
                                    this.updatingAddress = false;
                                    this.orderSvc.order = next;
                                    clearInterval(pull)
                                }
                                if (this.count === 6) {
                                    clearInterval(pull);
                                    this.systemError = true
                                }
                            },error=>{
                                clearInterval(pull)
                            })
                        }, 5000)
                        this.pulls.push(pull)
                    })
                }
            });
    }
    ngOnDestroy(): void {
        this.pulls.forEach(e=>{
            clearInterval(e)
        });
    }
    private addressIsDifferent(addressA: IAddress, addressB: IAddress) {
        return JSON.stringify(addressA) !== JSON.stringify(addressB)
    }
    ngOnInit() { }
    public calcTotal(): string {
        let sum = 0;
        this.orderSvc.order.productList.forEach(e => {
            sum = sum + +e.finalPrice;
        });
        this.orderSvc.order.paymentAmt = sum;
        return (+sum).toFixed(2);
    }
    public openAddressPicker() {
        const config = new MatBottomSheetConfig();
        config.data = { context: this.newOrder ? 'new' : 'update' };
        this.bottomSheet.open(BottomSheetAddressPickerComponent, config);
    }
    public openPaymentPicker() {
        this.bottomSheet.open(BottomSheetPaymentPickerComponent);
    }
    public placeOrder() {
        this.orderSvc.scrollTop.next();
        let createOrReplaceOrder: Observable<any>;
        if (this.newOrder) {
            createOrReplaceOrder = this.orderSvc.httpProxy.createOrder(this.orderSvc.order, this.orderSvc.changeId)
        } else {
            createOrReplaceOrder = this.orderSvc.httpProxy.reserveOrder(this.orderSvc.order)
        }
        createOrReplaceOrder.subscribe(next => {
            this.cartSvc.cart = [];
            this.orderSvc.orderId = next.headers.get('location');
            this.router.navigate(['/payment']);
        });
    }
}
