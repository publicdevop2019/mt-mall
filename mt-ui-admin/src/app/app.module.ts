import { LayoutModule } from '@angular/cdk/layout';
import { OverlayModule } from '@angular/cdk/overlay';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatBadgeModule } from '@angular/material/badge';
import { MatBottomSheetModule } from '@angular/material/bottom-sheet';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatOptionModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTreeModule } from '@angular/material/tree';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { FormInfoService, MtFormBuilderModule } from 'mt-form-builder';
import 'mt-wc-product';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CustomLoader } from './clazz/locale/custom-loader';
import { enUS } from './clazz/locale/en-US';
import { zhHans } from './clazz/locale/zh-Hans';
import { BackButtonComponent } from './components/back-button/back-button.component';
import { CardNotificationComponent } from './components/card-notification/card-notification.component';
import { CopyFieldComponent } from './components/copy-field/copy-field.component';
import { DynamicNodeComponent } from './components/dynamic-tree/dynamic-node/dynamic-node.component';
import { DynamicTreeComponent } from './components/dynamic-tree/dynamic-tree.component';
import { EditableBooleanComponent } from './components/editable-boolean/editable-boolean.component';
import { EditableFieldComponent } from './components/editable-field/editable-field.component';
import { EditableInputMultiComponent } from './components/editable-input-multi/editable-input-multi.component';
import { EditablePageSelectMultiComponent } from './components/editable-page-select-multi/editable-page-select-multi.component';
import { EditablePageSelectSingleComponent } from './components/editable-page-select-single/editable-page-select-single.component';
import { EditableSelectMultiComponent } from './components/editable-select-multi/editable-select-multi.component';
import { EditableSelectSingleComponent } from './components/editable-select-single/editable-select-single.component';
import { LazyImageComponent } from './components/lazy-image/lazy-image.component';
import { MsgBoxComponent } from './components/msg-box/msg-box.component';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { ObjectDetailComponent } from './components/object-detail/object-detail.component';
import { OperationConfirmDialogComponent } from './components/operation-confirm-dialog/operation-confirm-dialog.component';
import { PreviewOutletComponent } from './components/preview-outlet/preview-outlet.component';
import { ProgressSpinnerComponent } from './components/progress-spinner/progress-spinner.component';
import { ResolveConfirmDialogComponent } from './components/resolve-confirm-dialog/resolve-confirm-dialog.component';
import { SearchAttributeComponent } from './components/search-attribute/search-attribute.component';
import { SearchComponent } from './components/search/search.component';
import { TableColumnConfigComponent } from './components/table-column-config/table-column-config.component';
import { TreeComponent } from './components/tree/tree.component';
import { TreeNodeDirective } from './directive/tree-node.directive';
import { AttributeComponent } from './pages/attribute/attribute.component';
import { CatalogComponent } from './pages/catalog/catalog.component';
import { FilterComponent } from './pages/filter/filter.component';
import { LoginComponent } from './pages/login/login.component';
import { MessageCenterMallComponent } from './pages/message-center-mall/message-center-mall.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { OrderComponent } from './pages/order/order.component';
import { ProductComponent } from './pages/product/product.component';
import { SettingComponent } from './pages/setting/setting.component';
import { SummaryAttributeComponent } from './pages/summary-attribute/summary-attribute.component';
import { SummaryCatalogComponent } from './pages/summary-catalog/summary-catalog.component';
import { SummaryFilterComponent } from './pages/summary-filter/summary-filter.component';
import { SummaryOrderComponent } from './pages/summary-order/summary-order.component';
import { SummaryProductComponent } from './pages/summary-product/summary-product.component';
import { SummarySkuComponent } from './pages/summary-sku/summary-sku.component';
import { SummaryStoredEventComponent } from './pages/summary-stored-event/summary-stored-event.component';
import { SummaryTaskComponent } from './pages/summary-task/summary-task.component';
import { TaskComponent } from './pages/task/task.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { AuthService } from './services/auth.service';
import { DeviceService } from './services/device.service';
import { HttpProxyService } from './services/http-proxy.service';
import { CsrfInterceptor } from './services/interceptors/csrf.interceptor';
import { DeleteConfirmHttpInterceptor } from './services/interceptors/delete-confirm.interceptor';
import { CustomHttpInterceptor } from './services/interceptors/http.interceptor';
import { LoadingInterceptor } from './services/interceptors/loading.interceptor';
import { RequestIdHttpInterceptor } from './services/interceptors/request-id.interceptor';
import { SameRequestHttpInterceptor } from './services/interceptors/same-request.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NavBarComponent,
    ProgressSpinnerComponent,
    MsgBoxComponent,
    SummaryProductComponent,
    SummaryCatalogComponent,
    CatalogComponent,
    ProductComponent,
    SummaryOrderComponent,
    OrderComponent,
    BackButtonComponent,
    SummaryAttributeComponent,
    AttributeComponent,
    TreeComponent,
    OperationConfirmDialogComponent,
    SummaryFilterComponent,
    FilterComponent,
    SettingComponent,
    EditableFieldComponent,
    CopyFieldComponent,
    LazyImageComponent,
    PreviewOutletComponent,
    EditableSelectMultiComponent,
    EditableBooleanComponent,
    EditableSelectSingleComponent,
    EditableInputMultiComponent,
    SummarySkuComponent,
    SummaryTaskComponent,
    ObjectDetailComponent,
    EditablePageSelectSingleComponent,
    EditablePageSelectMultiComponent,
    MessageCenterMallComponent,
    DynamicTreeComponent,
    DynamicNodeComponent,
    TreeNodeDirective,
    SummaryStoredEventComponent,
    CardNotificationComponent,
    TaskComponent,
    ResolveConfirmDialogComponent,
    SearchComponent,
    SearchAttributeComponent,
    TableColumnConfigComponent,
    NotFoundComponent,
    WelcomeComponent,
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule,
    MatCheckboxModule,
    MatRadioModule,
    MatExpansionModule,
    MatCardModule,
    MatPaginatorModule,
    MatMenuModule,
    MatTableModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatSlideToggleModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTreeModule,
    MatStepperModule,
    MatBottomSheetModule,
    LayoutModule,
    MatChipsModule,
    MatSortModule,
    MatAutocompleteModule,
    MtFormBuilderModule,
    OverlayModule,
    MatBadgeModule,
    ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: CustomLoader
      }
    }),
  ],
  entryComponents: [
    MsgBoxComponent,
    CatalogComponent,
    AttributeComponent,
    ProductComponent,
    OperationConfirmDialogComponent,
    FilterComponent,
    ObjectDetailComponent,
    TreeComponent,
    SearchAttributeComponent,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SameRequestHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: DeleteConfirmHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CustomHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: LoadingInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: RequestIdHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CsrfInterceptor,
      multi: true
    },
    HttpProxyService, AuthService,CustomHttpInterceptor, FormInfoService, DeviceService],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(private translate: TranslateService, private fis: FormInfoService) {
    let lang = this.translate.currentLang
    if (lang === 'zhHans')
      this.fis.i18nLabel = zhHans
    if (lang === 'enUS')
      this.fis.i18nLabel = enUS
    this.translate.onLangChange.subscribe((e) => {
      if (e.lang === 'zhHans')
        this.fis.i18nLabel = zhHans
      if (e.lang === 'enUS')
        this.fis.i18nLabel = enUS
    })
  }
}
