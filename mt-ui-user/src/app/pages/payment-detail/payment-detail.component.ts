import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { toDataURL } from 'qrcode';
import { OrderService } from 'src/app/services/order.service';
import { Router } from '@angular/router';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
@Component({
  selector: 'app-payment-detail',
  templateUrl: './payment-detail.component.html',
  styleUrls: ['./payment-detail.component.scss']
})
export class PaymentDetailComponent implements OnInit {
  public paymentLink: string = '';
  public systemError: boolean=false;
  private count:number=1;
  constructor(private orderSvc: OrderService, private router: Router, private bar: SnackbarService, private titleSvc: Title,private trans:TranslateService) {
    this.trans.get(['PAYMENT_DETAIL','DOC_TITLE']).subscribe(next=>{
      this.titleSvc.setTitle(Object.values(next).join(' '))
    })
  }
  private qrFrame: ElementRef;
  @ViewChild('qrCodeFrame') set ft(ref: ElementRef) {
    if(ref){
      this.qrFrame=ref;
      toDataURL(this.qrFrame.nativeElement as HTMLCanvasElement, this.paymentLink)
    }
  };
  ngOnInit() {
    let pull = setInterval(() => {
      this.orderSvc.httpProxy.getOrderById(this.orderSvc.orderId).subscribe(next => {
        this.count++;
        if (next&&next.paymentLink) {
          this.paymentLink = next.paymentLink;
          clearInterval(pull)
        }
        if(this.count===6){
          console.dir('clear interval')
          clearInterval(pull);
          this.systemError=true
        }
      })

    }, 5000)
  }
  confirmPayment() {
    const orderId = this.orderSvc.orderId;
    this.orderSvc.httpProxy.confirmOrder(orderId).subscribe(next => {
      this.router.navigate(['/order-complete']);
    })
  }
}
