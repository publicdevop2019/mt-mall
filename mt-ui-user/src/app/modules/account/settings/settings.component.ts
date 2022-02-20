import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
@Component({
    selector: 'app-settings',
    templateUrl: './settings.component.html',
    styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
    constructor(public translate: TranslateService) { }

    ngOnInit() {
    }
    public toggleLang() {
      if (this.translate.currentLang === 'enUS') {
        this.translate.use('zhHans')
        this.translate.get('DOC_TITLE').subscribe(
          next => {
            document.title = next
            document.documentElement.lang = 'zh-Hans'
          }
        )
      }
      else {
        this.translate.use('enUS')
        this.translate.get('DOC_TITLE').subscribe(
          next => {
            document.title = next
            document.documentElement.lang = 'en'
          }
        )
      }
    }
}
