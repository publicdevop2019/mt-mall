import { Overlay, OverlayConfig } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef } from '@angular/material/bottom-sheet';
import { MatIcon } from '@angular/material/icon';
import { MatTableDataSource } from '@angular/material/table';
import { combineLatest } from 'rxjs';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { ObjectDetailComponent } from 'src/app/components/object-detail/object-detail.component';
import { OverlayService } from 'src/app/services/overlay.service';
import { IStoredEvent, StoredEventService } from 'src/app/services/stored-event.service';
import { IDtxDetail, ILtxDetail, TaskService } from 'src/app/services/task.service';
interface IStoredEventExt extends IStoredEvent {
  serviceName: string
}
@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
  displayedColumns: string[] = ['name', 'status', 'emptyOpt', 'skipped','retry'];
  eventDisplayedColumns: string[] = ['id', 'belongsTo', 'eventBody', 'timestamp', 'name', 'internal', 'retry'];
  dataSource: MatTableDataSource<ILtxDetail>;
  eventDataSource: MatTableDataSource<IStoredEventExt> = new MatTableDataSource([]);
  taskBottomSheet: IBottomSheet<IDtxDetail>;
  constructor(
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any, // keep as any is needed
    private bottomSheetRef: MatBottomSheetRef<TaskComponent>,
    public entitySvc: StoredEventService,
    public taskSvc: TaskService,
    private cdr: ChangeDetectorRef,
    private overlay: Overlay,
    private overlaySvc: OverlayService,
  ) {
    this.taskBottomSheet = data;
    const next: ILtxDetail[] = this.taskBottomSheet.from.ltx
    this.dataSource = new MatTableDataSource(next);
    this.entitySvc.resetServiceName();
    this.findEventsByDomainId();
  }

  ngOnInit(): void {
  }
  retry(id: string) {
    this.entitySvc.setServiceName('/saga-svc')
    this.entitySvc.retry(id).subscribe()
  }
  findEventsByDomainId() {
    combineLatest([
      this.entitySvc.getRelatedEvents(0, 100, 'domainId:' + this.taskBottomSheet.from.id, '/saga-svc'),
      this.entitySvc.getRelatedEvents(0, 100, 'domainId:' + this.taskBottomSheet.from.id, '/product-svc'),
    ])
      .subscribe(next => {
        const var0 = (next[0].data || []).map(e => {
          e['serviceName'] = '/saga-svc';
          return e as IStoredEventExt;
        })
        const var1 = (next[1].data || []).map(e => {
          e['serviceName'] = '/product-svc';
          return e as IStoredEventExt;
        })
        this.eventDataSource = new MatTableDataSource([...var0, ...var1]);
        this.cdr.detectChanges();
      })
  }
  dismiss(event: MouseEvent) {
    this.bottomSheetRef.dismiss();
    event.preventDefault();
  }
  doRetry(event: IStoredEventExt) {
    this.entitySvc.setServiceName(event.serviceName)
    this.entitySvc.retry(event.id).subscribe()
  }
  launchOverlay(el: MatIcon, data: IDtxDetail) {
    this.overlaySvc.data = data;
    let config = new OverlayConfig();
    config.hasBackdrop = true;
    config.positionStrategy = this.overlay.position().global().centerVertically().centerHorizontally();
    config.scrollStrategy = this.overlay.scrollStrategies.reposition();
    const overlayRef = this.overlay.create(config);
    const filePreviewPortal = new ComponentPortal(ObjectDetailComponent);
    overlayRef.attach(filePreviewPortal);
    overlayRef.backdropClick().subscribe(() => {
      overlayRef.dispose();
    })

  }
  doCancel(id: string) {
    this.taskSvc.doCancel(id).subscribe()
  }
}
