
export const environment = {
  production: false,
  mode: 'online' as 'online' | 'offline',
  APP_ID: '0C8HPGMON9J5',
  PROJECT_ID: '0P8HPG99R56P',
  APPP_SECRET_PUBLIC: '',
  authorzieUrl: 'http://localhost:4300/authorize?response_type=code&',
  productUrl:'http://localhost:4300/proxy/product-svc',
  imageUrl:'http://localhost:4300/proxy/product-svc/files',
  getTokenUri: 'http://localhost:4300/proxy/auth-svc/oauth/token',
  oauthRedirectUri: 'http://localhost:4200',
  profileUrl:'http://localhost:4300/proxy/profile-svc',
};
import 'zone.js/dist/zone-error';  // Included with Angular CLI.import { NgModel } from '@angular/forms';

