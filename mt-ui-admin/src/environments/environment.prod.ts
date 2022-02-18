export const environment = {
  production: true,
  mode: 'online' as 'online' | 'offline',
  validation: 'on' as 'on' | 'off',
  serverUri: 'https://api.duoshu.org',
  CLIENT_ID: '0C8HQM52YN7K',
  PROJECT_ID: '0P8HPG99R56P',
  CLIENT_SECRET_PUBLIC: '',
  AUTHORIZATION_URL: 'http://localhost:4300/authorize?response_type=code&',
  OAUTH2_REDIRECT_URL: 'http://localhost:4400',
  TOKEN_URL: 'http://localhost:4400/proxy/auth-svc/oauth/token',
};