import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import * as UUID from 'uuid/v1';
import { EntityCommonService } from '../clazz/entity.common-service';
import { INotification } from '../components/card-notification/card-notification.component';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
    providedIn: 'root'
})
export class MessageService extends EntityCommonService<INotification, INotification>{
    private SVC_NAME = '/product-svc';
    private ENTITY_NAME = '/mngmt/notifications';
    entityRepo: string = environment.serverUri + this.SVC_NAME + this.ENTITY_NAME;
    constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
        super(httpProxy, interceptor, deviceSvc);
    }
    public latestMessage: INotification[] = [];
    saveMessage(message: string) {
        if (this.latestMessage.length === 5) {
            this.latestMessage.splice(0, 1)
        }
        this.latestMessage.push(JSON.parse(message) as INotification);
    }
    private socket: WebSocket;
    connectNotification() {
        if (environment.mode !== 'offline') {
            const jwtBody = this.httpProxySvc.currentUserAuthInfo.access_token.split('.')[1];
            const raw = atob(jwtBody);
            this.httpProxySvc.createEntity('https://api.duoshu.org/auth-svc/tickets/0C8HPGLXHMET', null, UUID())
                .subscribe(next => {
                    this.socket = new WebSocket(`${this.getProtocal()}://${this.getPath()}/monitor?jwt=${btoa(next)}`);
                    this.socket.addEventListener('message', (event) => {
                        if (event.data !== '_renew')
                            this.saveMessage(event.data as string);
                    });
                });
        }
    }
    clear() {
        this.latestMessage = [];
    }
    private getProtocal() {
        let protocal = "ws"
        if (environment.serverUri.includes("https")) {
            protocal = "wss"
        }
        return protocal;
    }
    private getPath() {
        if (environment.serverUri.includes('localhost')) {
            return 'localhost:8083'
        }
        return environment.serverUri.replace('http://', '').replace("https://", "") + '/product-svc'
    }
}
