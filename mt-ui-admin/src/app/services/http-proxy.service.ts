import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import * as UUID from 'uuid/v1';
import { ISumRep } from '../clazz/summary.component';
import { IOrder, ITokenResponse } from '../clazz/validation/interfaze-common';
import { hasValue } from '../clazz/validation/validator-common';
import { IEditBooleanEvent } from '../components/editable-boolean/editable-boolean.component';
import { IEditEvent } from '../components/editable-field/editable-field.component';
import { IEditInputListEvent } from '../components/editable-input-multi/editable-input-multi.component';
import { IEditListEvent } from '../components/editable-select-multi/editable-select-multi.component';
export interface IPatch {
    op: string,
    path: string,
    value?: any,
}
export interface IPatchCommand extends IPatch {
    expect: number,
}
export interface IUser{
    id:string
    email:string
    createdAt:string
}
@Injectable({
    providedIn: 'root'
})
export class HttpProxyService {
    inProgress = false;
    private PRODUCT_SVC_NAME = '/product-svc';
    private PROFILE_SVC_NAME = '/product-svc';
    private FILE_UPLOAD_SVC_NAME = '/product-svc';
    set currentUserAuthInfo(token: ITokenResponse) {
        if (token === undefined || token === null) {
            localStorage.setItem('jwt', undefined);
        } else {
            localStorage.setItem('jwt', JSON.stringify(token));
        }
    };
    get currentUserAuthInfo(): ITokenResponse | undefined {
        const jwtTokenStr: string = localStorage.getItem('jwt');
        if (jwtTokenStr !== 'undefined' && jwtTokenStr !== undefined) {
            return <ITokenResponse>JSON.parse(jwtTokenStr)
        } else {
            return undefined;
        }
    }
    getToken(code: string): Observable<ITokenResponse> {
        const header = new HttpHeaders().append(
            'Authorization',
            'Basic ' +
            btoa(environment.CLIENT_ID + ':' + environment.CLIENT_SECRET_PUBLIC)
        );
        const formData = new FormData();
        formData.append('grant_type', 'authorization_code');
        formData.append('code', code);
        formData.append('scope', environment.PROJECT_ID);
        formData.append('redirect_uri', environment.OAUTH2_REDIRECT_URL);
        return this._httpClient.post<ITokenResponse>(
            environment.TOKEN_URL,
            formData,
            { headers: header }
        );
    }
    // OAuth2 pwd flow
    constructor(private _httpClient: HttpClient) {
    }
    cancelDtx(repo: string, id: string) {
        return this._httpClient.post(repo + "/" + id + '/cancel', null);
    }
    resolveCancelDtx(repo: string, id: string, reason: string) {
        return this._httpClient.post(repo + "/" + id + '/resolve', { reason: reason });
    }
    retry(repo: string, id: string) {
        return this._httpClient.post(repo + "/" + id + '/retry', null);
    }
    getOrders(): Observable<IOrder[]> {
        return this._httpClient.get<IOrder[]>(environment.serverUri + this.PROFILE_SVC_NAME + '/orders');
    };
    uploadFile(file: File): Observable<string> {
        return new Observable<string>(e => {
            const formData: FormData = new FormData();
            formData.append('file', file, file.name);
            let headerConfig = new HttpHeaders();
            headerConfig = headerConfig.set('changeId', UUID())
            this._httpClient.post<void>(environment.serverUri + this.FILE_UPLOAD_SVC_NAME + '/files/app', formData, { observe: 'response', headers: headerConfig }).subscribe(next => {
                e.next(next.headers.get('location'));
            });
        })
    };
    updateProductStatus(id: string, status: 'AVAILABLE' | 'UNAVAILABLE', changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.patch(environment.serverUri + this.PRODUCT_SVC_NAME + '/products/admin/' + id, this.getTimeValuePatch(status), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    batchUpdateProductStatus(ids: string[], status: 'AVAILABLE' | 'UNAVAILABLE', changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.patch(environment.serverUri + this.PRODUCT_SVC_NAME + '/products/admin', this.getTimeValuePatch(status, ids), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    batchUpdateUserStatus(entityRepo: string, ids: string[], status: 'LOCK' | 'UNLOCK', changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo, this.getUserStatusPatch(status, ids), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }

    private getPageParam(pageNumer?: number, pageSize?: number, sortBy?: string, sortOrder?: string): string {
        let var1: string[] = [];
        if (hasValue(pageNumer) && hasValue(pageSize)) {
            if (sortBy && sortOrder) {
                var1.push('num:' + pageNumer)
                var1.push('size:' + pageSize)
                var1.push('by:' + sortBy)
                var1.push('order:' + sortOrder)
                return "page=" + var1.join(',')
            } else {
                var1.push('num:' + pageNumer)
                var1.push('size:' + pageSize)
                return "page=" + var1.join(',')
            }
        }
        return ''
    }
    private getQueryParam(params: string[]): string {
        params = params.filter(e => (e !== '') && (e !== null) && (e !== undefined))
        if (params.length > 0)
            return "?" + params.join('&')
        return ""
    }
    private getTimeValuePatch(status: 'AVAILABLE' | 'UNAVAILABLE', ids?: string[]): IPatch[] {
        let re: IPatch[] = [];
        if (ids && ids.length > 0) {
            ids.forEach(id => {
                if (status === "AVAILABLE") {
                    let startAt = <IPatch>{ op: 'add', path: "/" + id + '/startAt', value: String(Date.now()) }
                    let endAt = <IPatch>{ op: 'add', path: "/" + id + '/endAt' }
                    re.push(startAt, endAt)
                } else {
                    let startAt = <IPatch>{ op: 'add', path: "/" + id + '/startAt' }
                    re.push(startAt)
                }
            })
        } else {
            if (status === "AVAILABLE") {
                let startAt = <IPatch>{ op: 'add', path: '/startAt', value: String(Date.now()) }
                let endAt = <IPatch>{ op: 'add', path: '/endAt' }
                re.push(startAt, endAt)
            } else {
                let startAt = <IPatch>{ op: 'add', path: '/startAt' }
                re.push(startAt)
            }
        }
        return re;
    }
    private getUserStatusPatch(status: 'LOCK' | 'UNLOCK', ids: string[]): IPatch[] {
        let re: IPatch[] = [];
        ids.forEach(id => {
            let var0: IPatch;
            if (status === "LOCK") {
                var0 = <IPatch>{ op: 'replace', path: "/" + id + '/locked', value: true }
            } else {
                var0 = <IPatch>{ op: 'replace', path: "/" + id + '/locked', value: false }
            }
            re.push(var0)
        })
        return re;
    }
    private getPatchPayload(fieldName: string, fieldValue: IEditEvent): IPatch[] {
        let re: IPatch[] = [];
        let type = undefined;
        if (fieldValue.original) {
            type = 'replace'
        } else {
            type = 'add'
        }
        let startAt = <IPatch>{ op: type, path: "/" + fieldName, value: fieldValue.next }
        re.push(startAt)
        return re;
    }
    private getPatchPayloadAtomicNum(id: string, fieldName: string, fieldValue: IEditEvent): IPatchCommand[] {
        let re: IPatchCommand[] = [];
        let type = undefined;

        if (fieldValue.original >= fieldValue.next) {
            type = 'diff'
        } else {
            type = 'sum'
        }
        let startAt = <IPatchCommand>{ op: type, path: "/" + id + "/" + fieldName, value: Math.abs(+fieldValue.next - +fieldValue.original), expect: 1 }
        re.push(startAt)
        return re;
    }
    private getPatchListPayload(fieldName: string, fieldValue: IEditListEvent): IPatch[] {
        let re: IPatch[] = [];
        let type = 'replace';
        let startAt = <IPatch>{ op: type, path: "/" + fieldName, value: fieldValue.next.map(e => e.value) }
        re.push(startAt)
        return re;
    }
    private getPatchInputListPayload(fieldName: string, fieldValue: IEditInputListEvent): IPatch[] {
        let re: IPatch[] = [];
        let startAt: IPatch;
        if (fieldValue.original) {
            startAt = <IPatch>{ op: 'replace', path: "/" + fieldName, value: fieldValue.next }
        } else {
            startAt = <IPatch>{ op: 'add', path: "/" + fieldName, value: fieldValue.next }
        }
        re.push(startAt)
        return re;
    }
    private getPatchBooleanPayload(fieldName: string, fieldValue: IEditBooleanEvent): IPatch[] {
        let re: IPatch[] = [];
        let type = undefined;
        let startAt: IPatch;
        if (typeof fieldValue.original === 'boolean' && typeof fieldValue.original === 'boolean') {
            type = 'replace'
            startAt = <IPatch>{ op: type, path: "/" + fieldName, value: fieldValue.next }
        } else if (typeof fieldValue.original === 'boolean' && typeof fieldValue.original === 'undefined') {
            type = 'remove'
            startAt = <IPatch>{ op: type, path: "/" + fieldName }
        } else {
            type = 'add'
            startAt = <IPatch>{ op: type, path: "/" + fieldName, value: fieldValue.next }
        }
        re.push(startAt)
        return re;
    }
    createEntity(entityRepo: string, entity: any, changeId: string): Observable<string> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<string>(e => {
            this._httpClient.post(entityRepo, entity, { observe: 'response', headers: headerConfig }).subscribe(next => {
                e.next(next.headers.get('location'));
            });
        });
    };
    readEntityById<S>(entityRepo: string, id: string, headers?: {}): Observable<S> {
        let headerConfig = new HttpHeaders();
        headers && Object.keys(headers).forEach(e => {
            headerConfig = headerConfig.set(e, headers[e] + '')
        })
        return this._httpClient.get<S>(entityRepo+ '/' + id, { headers: headerConfig });
    };
    readEntityByQuery<T>(entityRepo: string, num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
        let headerConfig = new HttpHeaders();
        headers && Object.keys(headers).forEach(e => {
            headerConfig = headerConfig.set(e, headers[e] + '')
        })
        return this._httpClient.get<ISumRep<T>>(entityRepo  + this.getQueryParam([this.addPrefix(query), this.getPageParam(num, size, by, order)]), { headers: headerConfig })
    }
    addPrefix(query: string): string {
        let var0: string = query;
        if (!query) {
            var0 = undefined
        } else {
            var0 = 'query=' + var0;
        }
        return var0
    }
    updateEntity(entityRepo: string, id: string, entity: any, changeId: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.put(entityRepo  + '/' + id, entity, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    updateEntityExt(entityRepo: string, id: string, entity: any, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return this._httpClient.put(entityRepo + '/' + id, entity, { headers: headerConfig });
    };
    deleteEntityById(entityRepo: string, id: string, changeId: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.delete(entityRepo + '/' + id, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    deleteVersionedEntityById(entityRepo: string, id: string, changeId: string, version: number): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        headerConfig = headerConfig.set('version', version + "")
        return new Observable<boolean>(e => {
            this._httpClient.delete(entityRepo + '/' + id, { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    deleteEntityByQuery(entityRepo: string, query: string, changeId: string): Observable<boolean> {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('changeId', changeId)
        return new Observable<boolean>(e => {
            this._httpClient.delete(entityRepo + '?' + this.addPrefix(query), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    };
    patchEntityById(entityRepo: string, id: string, fieldName: string, editEvent: IEditEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo + '/' + id, this.getPatchPayload(fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    patchEntityAtomicById(entityRepo: string, id: string, fieldName: string, editEvent: IEditEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo, this.getPatchPayloadAtomicNum(id, fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    patchEntityListById(entityRepo: string, id: string, fieldName: string, editEvent: IEditListEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo + '/' + id, this.getPatchListPayload(fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    patchEntityInputListById(entityRepo: string, id: string, fieldName: string, editEvent: IEditInputListEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo + '/' + id, this.getPatchInputListPayload(fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
    patchEntityBooleanById(entityRepo: string, id: string, fieldName: string, editEvent: IEditBooleanEvent, changeId: string) {
        let headerConfig = new HttpHeaders();
        headerConfig = headerConfig.set('Content-Type', 'application/json-patch+json')
        headerConfig = headerConfig.set('changeId', changeId);
        if (typeof editEvent.original === 'undefined' && typeof editEvent.next === 'undefined')
            return new Observable<boolean>(e => e.next(true));
        return new Observable<boolean>(e => {
            this._httpClient.patch(entityRepo + '/' + id, this.getPatchBooleanPayload(fieldName, editEvent), { headers: headerConfig }).subscribe(next => {
                e.next(true)
            });
        });
    }
}