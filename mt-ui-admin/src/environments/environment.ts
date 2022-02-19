// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  mode: 'online' as 'online' | 'offline',
  validation: 'on' as 'on' | 'off', //make payload validation always return true, note this will not disable all validations, only validator-helper is bypassed
  serverUri: 'http://localhost:4400/proxy',
  CLIENT_ID: '0C8HQM52YN7K',
  PROJECT_ID: '0P8HPG99R56P',
  CLIENT_SECRET_PUBLIC: '',
  AUTHORIZATION_URL: 'https://admin.duoshu.org/authorize?response_type=code&',
  OAUTH2_REDIRECT_URL: 'http://localhost:4400',
  TOKEN_URL: 'https://api.duoshu.org/auth-svc/oauth/token',
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
import 'zone.js/dist/zone-error';  // Included with Angular CLI.
