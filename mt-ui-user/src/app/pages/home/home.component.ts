import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
    constructor(private titleSvc: Title,private trans:TranslateService) {
        this.trans.get(['HOME','DOC_TITLE']).subscribe(next=>{
            this.titleSvc.setTitle(Object.values(next).join(' '))
          })
    }

    ngOnInit() {}
}
