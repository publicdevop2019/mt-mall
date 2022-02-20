import { Injectable } from '@angular/core';
import { OrderService } from './order.service';
import { Router } from '@angular/router';
import { SnackbarService } from './snackbar.service';
import { nullOrUndefined } from '../classes/utility';

@Injectable({
  providedIn: 'root'
})
export class QRService {

  constructor(private router: Router, private orderSvc: OrderService,private snakBarSvc:SnackbarService) { }
  canActivate(): boolean {
    if (nullOrUndefined(this.orderSvc.orderId) || this.orderSvc.orderId === '') {
      this.snakBarSvc.openSnackBar('PAYMENT_LINK_NOT_FOUND')
      this.router.navigate(['/home']);
    } else {
      return true
    }
  }
}
