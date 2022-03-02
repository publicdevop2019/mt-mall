import { HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { getCookie, logout } from '../../clazz/utility';
import { HttpProxyService } from '../http-proxy.service';
/**
 * use refresh token if call failed
 */
@Injectable()
export class CustomHttpInterceptor implements HttpInterceptor {
  private _errorStatus: number[] = [500, 503, 502, 504];
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  constructor(private router: Router, private _httpProxy: HttpProxyService, private _snackBar: MatSnackBar, private translate: TranslateService) { }
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
    if (this._httpProxy.currentUserAuthInfo && this._httpProxy.currentUserAuthInfo.access_token)
      if (
        (req.url.indexOf('oauth/token') > -1 && req.method === 'POST')
        ||
        (req.url.indexOf('csrf') > -1 && req.method === 'GET')
      ) {
        /**
         * skip Bearer header for public urls
         */
      } else {
        req = req.clone({ setHeaders: { Authorization: `Bearer ${this._httpProxy.currentUserAuthInfo.access_token}` }});
      }
    return next.handle(req)
      .pipe(catchError((error: HttpErrorResponse) => {
        if (error && error.status === 401) {
          if (this._httpProxy.currentUserAuthInfo === undefined || this._httpProxy.currentUserAuthInfo === null) {
            /** during log in call */
            this.openSnackbar('BAD_USERNAME_OR_PASSWORD');
            return throwError(error);
          }
          this.openSnackbar('SESSION_EXPIRED');
          logout(this.router);
          return throwError(error);
        }
        else if (this._errorStatus.indexOf(error.status) > -1) {
          this.openSnackbar('SERVER_RETURN_5XX');
        } else if (error.status === 404) {
          this.openSnackbar('URL_NOT_FOUND');
          return throwError(error);
        } else if (error.status === 405) {
          this.openSnackbar('METHOD_NOT_SUPPORTED');
          return throwError(error);
        } else if (error.status === 403) {
          return next.handle(req).pipe(catchError((error: HttpErrorResponse) => {
            this.openSnackbar('ACCESS_IS_NOT_ALLOWED');
            return throwError(error);
          }));
        } else if (error.status === 400) {
          this.openSnackbar('INVALID_REQUEST');
          return throwError(error);
        } else if (error.status === 0) {
          this.openSnackbar('NETWORK_CONNECTION_FAILED');
          return throwError(error);
        } else {
          return throwError(error);
        }

      })

      );
  }
  updateReqAuthToken(req: HttpRequest<any>): HttpRequest<any> {
    return req = req.clone({ setHeaders: { Authorization: `Bearer ${this._httpProxy.currentUserAuthInfo.access_token}` } })
  }
  openSnackbar(message: string) {
    this.translate.get(message).subscribe(next => {
      this._snackBar.open(next, 'OK', {
        duration: 5000,
      });
    })
  }
}