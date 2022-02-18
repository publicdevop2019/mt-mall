import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { LoginComponent } from './pages/login/login.component';
import { MessageCenterMallComponent } from './pages/message-center-mall/message-center-mall.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { OrderComponent } from './pages/order/order.component';
import { SettingComponent } from './pages/setting/setting.component';
import { SummaryAttributeComponent } from './pages/summary-attribute/summary-attribute.component';
import { SummaryCatalogComponent } from './pages/summary-catalog/summary-catalog.component';
import { SummaryFilterComponent } from './pages/summary-filter/summary-filter.component';
import { SummaryOrderComponent } from './pages/summary-order/summary-order.component';
import { SummaryProductComponent } from './pages/summary-product/summary-product.component';
import { SummarySkuComponent } from './pages/summary-sku/summary-sku.component';
import { SummaryStoredEventComponent } from './pages/summary-stored-event/summary-stored-event.component';
import { SummaryTaskComponent } from './pages/summary-task/summary-task.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { AuthService } from './services/auth.service';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard', component: NavBarComponent, canActivateChild: [AuthService],
    children: [
      { path: '', redirectTo: 'welcome', pathMatch: 'full' },
      { path: 'welcome', component: WelcomeComponent },
      { path: 'orders', component: SummaryOrderComponent },
      { path: 'orders/:id', component: OrderComponent },
      { path: 'products', component: SummaryProductComponent },
      { path: 'catalogs/:type', component: SummaryCatalogComponent },
      { path: 'filters', component: SummaryFilterComponent },
      { path: 'tasks', component: SummaryTaskComponent },
      { path: 'attributes', component: SummaryAttributeComponent },
      { path: 'events', component: SummaryStoredEventComponent },
      { path: 'settings', component: SettingComponent},
      { path: 'mall-message-center', component: MessageCenterMallComponent},
      { path: 'skus', component: SummarySkuComponent},
      { path: '**', component: NotFoundComponent }
    ]
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', component: LoginComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
