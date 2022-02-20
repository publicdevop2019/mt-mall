import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-catalogs',
    templateUrl: './catalogs.component.html',
    styleUrls: ['./catalogs.component.scss']
})
export class CatalogsComponent implements OnInit {
    constructor(private titleSvc: Title,private trans:TranslateService) {
        this.trans.get(['CATALOGS','DOC_TITLE']).subscribe(next=>{
            this.titleSvc.setTitle(Object.values(next).join(' '))
          })
    }

    ngOnInit() { }
}
