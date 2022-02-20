import { Component, OnInit, ElementRef, ViewChild, Inject, LOCALE_ID } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap, mergeMap } from 'rxjs/operators';
import { CartService } from 'src/app/services/cart.service';
import { ProductListService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { Title } from '@angular/platform-browser';
import { ICartItem } from '../cart/cart.component';
import { TranslateService } from '@ngx-translate/core';
export interface IProductSimple {
    imageUrlSmall: string;
    name: string;
    description: string;
    lowestPrice: number;
    totalSales: number;
    id: string;
}
export interface IProductOptions {
    title: string;
    options: IProductOption[];
}
export interface IProductOption {
    optionValue: string;
    priceVar?: string;
}
export interface IProductSku {
    attributesSales: string[];
    price: number;
    storage: number;
    skuId: string
}
export interface IProductDetail extends IProductSimple {
    imageUrlLarge?: string[];
    selectedOptions?: IProductOptions[];
    specification?: string[];
    skus: IProductSku[],
    storage?: number,
    attrIdMap: { [key: number]: string }
    attributeSaleImages?: IAttrImage[]
}
export interface IAttrImage {
    attributeSales: string,
    imageUrls: string[]
}
@Component({
    selector: 'app-product-detail',
    templateUrl: './product-detail.component.html',
    styleUrls: ['./product-detail.component.scss'],
})
export class ProductDetailComponent implements OnInit {
    @ViewChild('product') product: ElementRef;
    public cartItem: ICartItem;
    public productDetail: IProductDetail;
    constructor(
        private activatedRoute: ActivatedRoute,
        private cartSvc: CartService,
        private productListSvc: ProductListService,
        private snackBarSvc: SnackbarService,
        private titleSvc: Title,
        @Inject(LOCALE_ID) locale: string,
        private trans:TranslateService
    ) {
        this.trans.get(['PRODUCT_DETAIL','DOC_TITLE']).subscribe(next=>{
            this.titleSvc.setTitle(Object.values(next).join(' '))
          })
        this.activatedRoute.paramMap
            .pipe(
                switchMap(next => {
                    return this.productListSvc.httpProxy.getProductDetailsById(
                        next.get('productId')
                    );
                })
            )
            .subscribe(next => {
                if (next.skus.length === 1 && next.skus[0].attributesSales && next.skus[0].attributesSales[0] === '') {
                    this.cartItem = {
                        finalPrice: next.lowestPrice + "",
                        selectedOptions: [],
                        attributesSales: [],
                        imageUrlSmall: next.imageUrlSmall,
                        productId: next.id,
                        name: next.name,
                        attrIdMap: {},
                        id: '',
                        skuId: next.skus[0].skuId,
                        amount: 1,
                    }
                }
                const popupEl = document.createElement('mt-wc-product');
                (popupEl as any).productDetail = next;
                (popupEl as any).locale = locale.replace('-', '');
                (popupEl as any).imgSize = '100vw';
                this.productDetail = next;
                popupEl.addEventListener('valueChanged', (e) => {
                    this.cartItem = (e as CustomEvent).detail;
                });
                this.product.nativeElement.appendChild(popupEl)
                this.trans.get(['PRODUCT_DETAIL','DOC_TITLE']).subscribe(next1=>{
                    this.titleSvc.setTitle([next.name,...Object.values(next1)].join(' '))
                  })
            });
    }
    ngOnInit() { }
    public addToCart() {
        this.cartSvc.httpProxy
            .addToCart(this.cartItem).pipe(mergeMap(next => {
                this.snackBarSvc.openSnackBar('ITEM_ADDED');
                return this.cartSvc.httpProxy.getCartItems()
            }))
            .subscribe(next => {
                this.cartSvc.cart = next.data;
            });
    }
}
