import { IList } from '../components/footer/footer.component';

export const CONFIG_FOOTER: IList[] = [
    {
        icon: 'home',
        title: 'home',
        routerUrl: 'home'
    },
    {
        icon: 'category',
        title: 'catalogs',
        routerUrl: 'catalogs'
    },
    {
        icon: 'shopping_cart',
        title: 'cart',
        routerUrl: 'cart'
    },
    {
        icon: 'account_box',
        title: 'account',
        routerUrl: 'account'
    }
];

export const CONFIG_ACCOUNT: IList[] = [
    {
        icon: 'local_shipping',
        title: 'shipping_address',
        routerUrl: 'addresses'
    },
    {
        icon: 'assignment',
        title: 'orders',
        routerUrl: 'orders'
    },
    {
        icon: 'settings',
        title: 'settings',
        routerUrl: 'settings'
    }
];
