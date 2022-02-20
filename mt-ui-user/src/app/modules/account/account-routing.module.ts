import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AccountComponent } from './account.component';
import { AddressesComponent } from './addresses/addresses.component';
import { FormAddressComponent } from './form-address/form-address.component';
import { OrdersComponent } from 'src/app/modules/account/orders/orders.component';
import { OrderDetailComponent } from 'src/app/modules/account/order-detail/order-detail.component';
import { SettingsComponent } from './settings/settings.component';

const routes: Routes = [
  { path: '', component: AccountComponent, data: { animation: 'account' } },
  { path: 'addresses', component: AddressesComponent, data: { animation: 'addresses' } },
  { path: 'address', component: FormAddressComponent, data: { animation: 'address' } },
  { path: 'addresses/:addressId', component: FormAddressComponent, data: { animation: 'addressDetail' } },
  { path: 'orders', component: OrdersComponent, data: { animation: 'orders' } },
  { path: 'settings', component: SettingsComponent, data: { animation: 'settings' } },
  { path: 'orders/:orderId', component: OrderDetailComponent, data: { animation: 'orderDetail' } },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AccountRoutingModule { }
