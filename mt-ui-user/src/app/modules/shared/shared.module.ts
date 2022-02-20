import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { BtnComponent } from './btn/btn.component';
import { CardCartComponent } from './card-cart/card-cart.component';
import { LazyImageComponent } from './lazy-image/lazy-image.component';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule } from '@ngx-translate/core';



@NgModule({
  declarations: [
    BtnComponent,
    CardCartComponent,
    LazyImageComponent
  ],
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule
  ],
  exports: [
    BtnComponent,
    CardCartComponent,
    LazyImageComponent,
    TranslateModule
  ]
})
export class SharedModule { }
