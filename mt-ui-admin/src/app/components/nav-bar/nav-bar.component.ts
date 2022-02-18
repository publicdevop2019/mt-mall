import { MediaMatcher } from '@angular/cdk/layout';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { logout } from 'src/app/clazz/utility';
import { AuthService } from 'src/app/services/auth.service';
import { DeviceService } from 'src/app/services/device.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MessageService } from 'src/app/services/message.service';
export interface INavElement {
  link: string;
  icon?: string;
  display: string;
  params: any
}

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {
  msgDetails: boolean = false;
  menuOpen: boolean = false;
  mobileQuery: MediaQueryList;
  menuMisc: INavElement[] = [
    {
      link: 'settings',
      display: 'SYSTEM_SETTINGS',
      icon: 'settings',
      params: {
      },
    },
  ];
  menuMall: INavElement[] = [
    {
      link: 'welcome',
      display: 'WELCOME',
      icon: 'dashboard',
      params: {
      },
    },
    {
      link: 'products',
      display: 'PRODUCT_DASHBOARD',
      icon: 'storefront',
      params: {
      },
    },
    {
      link: 'skus',
      display: 'SKU_DASHBOARD',
      icon: 'storage',
      params: {
      },
    },
    {
      link: 'catalogs/frontend',
      display: 'CATEGORY_DASHBOARD',
      icon: 'category',
      params: {
      },
    },
    {
      link: 'catalogs/backend',
      display: 'CATEGORY_ADMIN_DASHBOARD',
      icon: 'store',
      params: {
      },
    },
    {
      link: 'attributes',
      display: 'ATTRIBUTE_DASHBOARD',
      icon: 'subject',
      params: {
      },
    },
    {
      link: 'filters',
      display: 'FILTER_DASHBOARD',
      icon: 'filter_list',
      params: {
      },
    },
    {
      link: 'orders',
      display: 'ORDER_DASHBOARD',
      icon: 'assignment',
      params: {
      },
    },
    {
      link: 'tasks',
      display: 'TASK_DASHBOARD',
      icon: 'import_contacts',
      params: {
      },
    },
    {
      link: 'events',
      display: 'EVENT_DASHBOARD',
      icon: 'event_available',
      params: {
      },
    },
    {
      link: 'mall-message-center',
      display: 'MALL_MSG_DASHBOARD',
      icon: 'message',
      params: {
      },
    },
  ];
  private _mobileQueryListener: () => void;
  @ViewChild("snav", { static: true }) snav: MatSidenav;
  constructor(public authSvc:AuthService, public httpProxySvc: HttpProxyService, changeDetectorRef: ChangeDetectorRef, media: MediaMatcher, public route: ActivatedRoute, public router: Router, public translate: TranslateService, public deviceSvc: DeviceService, public msgSvc: MessageService) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this._mobileQueryListener = () => changeDetectorRef.detectChanges();
    this.mobileQuery.addListener(this._mobileQueryListener);
  }
  openedHandler(panelName: string) {
    localStorage.setItem(panelName, 'true')
  }
  closedHander(panelName: string) {
    localStorage.setItem(panelName, 'false')
  }
  navExpand(panelName: string) {
    return localStorage.getItem(panelName) === 'true'
  }
  ngOnDestroy(): void {
    this.mobileQuery.removeListener(this._mobileQueryListener);
  }
  ngOnInit() {
    this.msgSvc.connectMallMonitor();
  }
  doLogout() {
    logout()
  }
  preserveURLQueryParams(input: INavElement) {
    const var0 = this.route.snapshot.queryParams;
    if (this.router.url.includes(input.link)) {
      return {
        ...input.params,
        ...var0
      }
    } else {
      return {
        ...input.params,
      }
    }
  }
}
