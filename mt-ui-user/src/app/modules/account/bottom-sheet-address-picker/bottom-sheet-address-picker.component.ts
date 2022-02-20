import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { delay, switchMap } from 'rxjs/operators';
import { IAddress } from 'src/app/modules/account/addresses/addresses.component';
import { AddressService } from 'src/app/services/address.service';
import { OrderService } from 'src/app/services/order.service';

@Component({
    selector: 'app-bottom-sheet-address-picker',
    templateUrl: './bottom-sheet-address-picker.component.html',
    styleUrls: ['./bottom-sheet-address-picker.component.scss']
})
export class BottomSheetAddressPickerComponent implements OnInit {
    private orderBottomSheet: any;
    public address: IAddress[];
    constructor(
        public addressSvc: AddressService,
        private change: ChangeDetectorRef,
        private bottomSheetRef: MatBottomSheetRef<
            BottomSheetAddressPickerComponent
        >,
        @Inject(MAT_BOTTOM_SHEET_DATA) public data: any, // keep as any is needed
        private orderSvc: OrderService,
    ) {
        this.orderBottomSheet = data;
        this.addressSvc.getShippingAddress().subscribe(next => {
            this.address = next.data;
            this.change.detectChanges();
        });
    }
    public addressPicked(event: MouseEvent, address: IAddress): void {
        if (this.orderBottomSheet.context === 'new') {
            this.orderSvc.order.address = address;
        } else {
            const copy=JSON.parse(JSON.stringify(address));
            delete copy['id'];
            if(JSON.stringify(this.orderSvc.order.address)!==JSON.stringify(copy)){
                this.orderSvc.httpProxy.updateOrderAddress(this.orderSvc.order, address).subscribe(()=>{
                    this.orderSvc.updateAddress.next();
                })
            }
            
        }
        this.bottomSheetRef.dismiss();
        event.preventDefault();
    }
    public dismiss(): void {
        this.bottomSheetRef.dismiss();
    }
    ngOnInit() { }
}
