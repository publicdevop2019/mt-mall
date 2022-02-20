import { Component, OnInit } from '@angular/core';
import { switchMap } from 'rxjs/operators';
import { shrinkOutAnimation } from 'src/app/classes/animation';
import { AddressService } from 'src/app/services/address.service';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
export interface IAddress {
    id: string;
    country: string;
    province: string;
    postalCode: string;
    fullName: string;
    line1: string;
    line2: string;
    city: string;
    phoneNumber: string;
}
@Component({
    selector: 'app-addresses',
    templateUrl: './addresses.component.html',
    styleUrls: ['./addresses.component.scss'],
    animations: [shrinkOutAnimation]
})
export class AddressesComponent implements OnInit {
    public address: IAddress[];
    constructor(public addressSvc: AddressService, private titleSvc: Title,private trans:TranslateService) {
        this.trans.get(['SHIPPING_ADDRESS','ACCOUNT','DOC_TITLE']).subscribe(next1=>{
            this.titleSvc.setTitle(Object.values(next1).join(' '))
          })
        this.addressSvc.getShippingAddress().subscribe(next => {
            this.address = next.data;
        });
    }

    ngOnInit() { }
    public doDelete(id: string) {
        this.addressSvc
            .deleteAddress(id)
            .pipe(
                switchMap(next => {
                    return this.addressSvc.getShippingAddress();
                })
            )
            .subscribe(next => {
                /**
                 * below is required for animation to work
                 */
                this.address = this.address.filter(
                    address => address.id !== id
                );
            });
    }
}
