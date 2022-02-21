import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { environment } from 'src/environments/environment';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  constructor(
    public httpProxy: HttpProxyService,
    public dialog: MatDialog,
    private activeRoute: ActivatedRoute,
    private router: Router,
    public translate: TranslateService,
  ) {
    if (!this.httpProxy.currentUserAuthInfo) {
      this.activeRoute.queryParams
        .pipe(
          mergeMap(output => {
            if (output.code === undefined || output.code === null) {
              /** do nothing, wait for user trigger login */
              return of(undefined);
            } else {
              return this.httpProxy.getToken(output.code);
            }
          })
        ).pipe(
          mergeMap(authInfo => {
            if (authInfo) {
              this.httpProxy.currentUserAuthInfo = authInfo;
              /** remove one time code to prevent refresh issue */
              this.router.navigate([], {
                queryParams: {
                  code: null,
                },
                queryParamsHandling: 'merge'
              })
              return of(undefined);
            } else {
              return of(undefined);
              /** purposely do nothing */
            }
          })
        )
        .subscribe(()=>{
          router.navigate(['/dashboard/welcome'])
        }
        )


    } else {
      /**
       * do nothing
       */
    }
  }

  ngOnInit() {
    sessionStorage.clear();
    localStorage.removeItem('jwt');
  }
  login() {
    location.replace(
      `${environment.AUTHORIZATION_URL}client_id=${environment.CLIENT_ID}&redirect_uri=${environment.OAUTH2_REDIRECT_URL}&state=login&project_id=${environment.PROJECT_ID}`
    );
  }

  public toggleLang() {
    if (this.translate.currentLang === 'enUS') {
      this.translate.use('zhHans')
      this.translate.get('DOCUMENT_TITLE').subscribe(
        next => {
          document.title = next
          document.documentElement.lang = 'zh-Hans'
        }
      )
    }
    else {
      this.translate.use('enUS')
      this.translate.get('DOCUMENT_TITLE').subscribe(
        next => {
          document.title = next
          document.documentElement.lang = 'en'
        }
      )
    }
  }
}
