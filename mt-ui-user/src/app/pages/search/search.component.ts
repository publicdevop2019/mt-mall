import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { debounce, switchMap, filter, map } from 'rxjs/operators';
import { interval, Subscription } from 'rxjs';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { IProductSimple } from '../product-detail/product-detail.component';
import { FormSearchComponent } from 'src/app/components/form-search/form-search.component';
import { GhostService } from 'src/app/services/ghost.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit, AfterViewInit, OnDestroy {
  constructor(private httpProxy: HttpProxyService, private ghostSvc: GhostService, 
    private router: Router, private activeRoute: ActivatedRoute, private changeRef: ChangeDetectorRef,private titleSvc:Title,private trans:TranslateService) {
      this.trans.get(['SEARCH','DOC_TITLE']).subscribe(next=>{
        this.titleSvc.setTitle(Object.values(next).join(' '))
      })
  }
  private pageNumber = 0;
  public pageSize = 6;
  private searchKey = '';
  private sub0: Subscription;
  private sub1: Subscription;
  private sub2: Subscription;
  endOfPages = false;
  searchResults: IProductSimple[];
  @ViewChild('searchInput') searchComponnent: FormSearchComponent;
  ngAfterViewInit(): void {
    // sub for search event
    this.sub0 = this.searchComponnent.searchForm.get('search').valueChanges
      .pipe(debounce(() => interval(1000)))
      .pipe(filter(el => this.invalidSearchParam(el)))
      .pipe(map(el => el.trim()))
      .pipe(switchMap(e => {
        this.searchKey = e;
        this.pageNumber = 0;
        this.endOfPages = false;
        // update url search key
        this.router.navigate(
          [],
          {
            relativeTo: this.activeRoute,
            queryParams: { key: this.searchKey },
            queryParamsHandling: 'merge'
          });
        return this.httpProxy.searchProduct(e, this.pageNumber, this.pageSize)
      }))
      .subscribe(next => {
        this.searchResults = next.data;
      });
    this.sub2 = this.activeRoute.queryParamMap.subscribe(queryMaps => {
      if (queryMaps.get('key')) {
        const str = queryMaps.get('key')
        this.searchKey = str;
        this.searchComponnent.searchForm.get('search').setValue(this.searchKey);
        this.changeRef.detectChanges();
      }
    });
    // unsub to prevent duplicate search call after input change
    this.sub2.unsubscribe();
  }

  ngOnDestroy(): void {
    this.sub0.unsubscribe()
    this.sub1.unsubscribe()
  }
  ngOnInit() {
    // sub for infinite scroll
    this.sub1 = this.ghostSvc.productCardGhostObser
      .pipe(switchMap(() => {
        this.pageNumber++;
        return this.httpProxy.searchProduct(this.searchKey, this.pageNumber, this.pageSize)
      })).subscribe(next => {
        if (next.data.length < this.pageSize) {
          this.endOfPages = true;
        }
        this.searchResults.push(...next.data);
      })
  }
  private invalidSearchParam(input: string): boolean {
    const spaces: RegExp = new RegExp(/^\s*$/)
    return !spaces.test(input)
  }

}
