import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {GenerateComponent} from "./generate/generate.component";
import {NzButtonModule} from "ng-zorro-antd/button";
import {NzFormModule} from "ng-zorro-antd/form";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NzInputModule} from "ng-zorro-antd/input";
import {NzSpinModule} from "ng-zorro-antd/spin";
import {NzRadioModule} from "ng-zorro-antd/radio";
import {NzSelectModule} from "ng-zorro-antd/select";
import {NzUploadModule} from "ng-zorro-antd/upload";
import {NzModalModule} from "ng-zorro-antd/modal";
import {NzDividerModule} from "ng-zorro-antd/divider";
import {NzPopconfirmModule} from "ng-zorro-antd/popconfirm";
import {NzTableModule} from "ng-zorro-antd/table";
import {NzMessageModule} from "ng-zorro-antd/message";
import {DocsComponent} from './docs/docs.component';
import {MarkdownModule} from "ngx-markdown";


@NgModule({
  declarations: [GenerateComponent, DocsComponent],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NzButtonModule,
    NzFormModule,
    NzInputModule,
    NzSpinModule,
    NzRadioModule,
    NzSelectModule,
    NzUploadModule,
    NzModalModule,
    NzDividerModule,
    NzPopconfirmModule,
    NzTableModule,
    NzMessageModule,
    MarkdownModule.forRoot()
  ]
})
export class PagesModule {
}
