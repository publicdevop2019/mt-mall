import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
    providedIn: 'root'
})
export class SnackbarService {
    durationInSeconds = 2;
    constructor(private snackBar: MatSnackBar,private trans:TranslateService) {
    }
    openSnackBar(announcement: string) {
        this.trans.get([announcement,'OK']).subscribe(next=>{
            const var0=Object.values(next) as string[]
            this.snackBar.open(var0[0], var0[1], {
                duration: this.durationInSeconds * 1000,
                verticalPosition: 'top',
            });
        })
    }
}
