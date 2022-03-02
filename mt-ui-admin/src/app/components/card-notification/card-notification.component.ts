import { Component, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { IIdBasedEntity } from 'src/app/clazz/summary.component';
export interface INotification extends IIdBasedEntity{
  title:string;
  descriptions:string[]
  date:number
}
@Component({
  selector: 'app-card-notification',
  templateUrl: './card-notification.component.html',
  styleUrls: ['./card-notification.component.css']
})
export class CardNotificationComponent implements OnInit {
  @Input() value: INotification;

  constructor() {
  }
  ngOnInit(): void {
  }
}
