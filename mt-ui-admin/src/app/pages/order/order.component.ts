import { Component, Inject, OnDestroy } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { IForm } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import { IBottomSheet, ISumRep } from 'src/app/clazz/summary.component';
import { ORDER_ADDRESS_CONFIG, ORDER_DETAIL_CONFIG, ORDER_PRODUCT_CONFIG, ORDER_TASK_CONFIG } from 'src/app/form-configs/order.config';
import { IAddress, ICartItem, IOrder } from 'src/app/clazz/validation/interfaze-common';
import { OrderService } from 'src/app/services/order.service';
import { IDtxDetail, TaskService } from 'src/app/services/task.service';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css']
})
export class OrderComponent implements OnDestroy {
  formIdOrder = 'orderDetail';
  formInfoOrder: IForm = JSON.parse(JSON.stringify(ORDER_DETAIL_CONFIG));
  formIdTask = 'orderTask';
  formInfoTask: IForm = JSON.parse(JSON.stringify(ORDER_TASK_CONFIG));
  formIdAddress = 'orderAddress';
  formInfoAddress: IForm = JSON.parse(JSON.stringify(ORDER_ADDRESS_CONFIG));
  formIdProduct = 'orderProduct';
  formInfoProduct: IForm = JSON.parse(JSON.stringify(ORDER_PRODUCT_CONFIG));
  orderBottomSheet: IBottomSheet<IOrder>;
  private formCreatedOb1: Observable<string>;
  private formCreatedOb2: Observable<string>;
  private formCreatedOb3: Observable<string>;
  private formCreatedOb4: Observable<string>;
  constructor(
    public orderSvc: OrderService,
    public taskSvc: TaskService,
    private fis: FormInfoService,
    private tSvc: TranslateService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any, // keep as any is needed
    private _bottomSheetRef: MatBottomSheetRef<OrderComponent>,
  ) {
    this.orderBottomSheet = data;
    this.formCreatedOb1 = this.fis.formCreated(this.formIdOrder);
    this.formCreatedOb2 = this.fis.formCreated(this.formIdAddress);
    this.formCreatedOb3 = this.fis.formCreated(this.formIdProduct);
    this.formCreatedOb4 = this.fis.formCreated(this.formIdTask);
    combineLatest([this.formCreatedOb1, this.formCreatedOb2, this.formCreatedOb3]).pipe(take(1)).subscribe(next => {
      let parsedPayload: any = {}
      Object.assign(parsedPayload, this.orderBottomSheet.from);
      let ob: Observable<any>;
      if (this.orderBottomSheet.from.paid) {
        ob = this.tSvc.get('PAID')
      } else {
        ob = this.tSvc.get('NOT_PAID')
      }
      let ob2 = this.tSvc.get(this.orderBottomSheet.from.orderState);
      combineLatest([ob, ob2]).subscribe(e => {
        parsedPayload['paid'] = e[0];
        parsedPayload['orderState'] = e[1];
        this.fis.formGroupCollection[this.formIdOrder].patchValue(parsedPayload);
      });
      this.fis.formGroupCollection[this.formIdAddress].patchValue(this.convertAddress(this.orderBottomSheet.from.address));
      let var0 = this.beforePatchProduct(this.orderBottomSheet.from.productList);
      let value0 = this.fis.parsePayloadArr(var0.map(e => e.productId), 'productId');
      let value1 = this.fis.parsePayloadArr(var0.map(e => e.name), 'name');
      let value2 = this.fis.parsePayloadArr(var0.map(e => e.finalPrice), 'finalPrice');
      let value3 = this.fis.parsePayloadArr(var0.map(e => e.attributesSales.join(",")), 'attributesSales');
      let value4 = this.fis.parsePayloadArr(var0.map(e => e.imageUrlSmall), 'imageUrlSmall');
      let value5 = this.fis.parsePayloadArr(var0.map(e => e.selectedOptions.map(e => e.title + ":" + e.options[0].optionValue + "-" + e.options[0].priceVar).join(",")), 'selectedOptions');
      Object.assign(value0, value1)
      Object.assign(value0, value2)
      Object.assign(value0, value3)
      Object.assign(value0, value4)
      Object.assign(value0, value5)
      this.fis.restoreDynamicForm(this.formIdProduct, value0, var0.length)
    });
    const var0 = this.taskSvc.getAllDtx(0, 50, "orderId:" + this.orderBottomSheet.from.id)

    combineLatest([var0, this.formCreatedOb4]).pipe(take(1)).subscribe(next => {
      let combined = [
        ...(next[0] as ISumRep<IDtxDetail>).data,
      ];
      let value0 = this.fis.parsePayloadArr(combined.map(e => e.createdAt), 'createdAt');
      let value2 = this.fis.parsePayloadArr(combined.map(e => e.id), 'id');
      let value8 = this.fis.parsePayloadArr(combined.map(e => e.changeId), 'transactionId');
      const names = [
        ...(next[0] as ISumRep<IDtxDetail>).data.map((e) => e.name),
      ].filter(e => e)
      // let value9 = this.fis.parsePayloadArr(name, 'taskName');
      const translatedStatus = combined.map(e => this.tSvc.get(e.status));
      const translatedName = names.map(e => this.tSvc.get(e));

      combineLatest([...translatedStatus]).subscribe(next => {
        let value6 = this.fis.parsePayloadArr(next, 'taskStatus');
        Object.assign(value0, value6);
        Object.assign(value0, value2);
        Object.assign(value0, value8);
        combineLatest([...translatedName]).subscribe(next2 => {
          let value7 = this.fis.parsePayloadArr(next2, 'taskName');
          Object.assign(value0, value7);
          this.fis.restoreDynamicForm(this.formIdTask, value0, combined.length)
        })
      })
    })
  }
  convertAddress(address: IAddress): { [key: string]: any; } {
    return {
      orderAddressCity: address.city,
      orderAddressCountry: address.country,
      orderAddressFullName: address.fullName,
      orderAddressLine1: address.line1,
      orderAddressLine2: address.line2,
      orderAddressPhoneNumber: address.phoneNumber,
      orderAddressPostalCode: address.postalCode,
      orderAddressProvince: address.province
    }
  }
  dismiss(event: MouseEvent) {
    this._bottomSheetRef.dismiss();
    event.preventDefault();
  }
  ngOnDestroy(): void {
    this.fis.resetAll();
  }
  beforePatchProduct(productList: ICartItem[]): ICartItem[] {
    return productList.map(e => {
      e.attributesSales = e.attributesSales.map(ee => {
        if(ee){
          let attrId = ee.split(":")[0];
          let value = ee.split(":")[1];
          return e.attrIdMap[attrId] + ":" + value
        }else{
          return ee;
        }
      });
      return e;
    })
  }
}
