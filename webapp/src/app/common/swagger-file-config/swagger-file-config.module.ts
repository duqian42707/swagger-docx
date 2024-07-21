import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SwaggerFileConfigComponent } from './swagger-file-config.component';
import {NzUploadModule} from "ng-zorro-antd/upload";
import {NzButtonModule} from "ng-zorro-antd/button";
import {NzIconModule} from "ng-zorro-antd/icon";



@NgModule({
  declarations: [
    SwaggerFileConfigComponent
  ],
  imports: [
    CommonModule,
    NzUploadModule,
    NzButtonModule,
    NzIconModule
  ],
  exports:[
    SwaggerFileConfigComponent
  ]
})
export class SwaggerFileConfigModule { }
