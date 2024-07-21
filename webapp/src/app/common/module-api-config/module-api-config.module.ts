import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ModuleApiConfigComponent} from './module-api-config.component';
import {NzButtonModule} from "ng-zorro-antd/button";
import {NzTreeModule} from "ng-zorro-antd/tree";
import { ModuleManagementComponent } from './module-management/module-management.component';
import {NzGridModule} from "ng-zorro-antd/grid";
import {NzIconModule} from "ng-zorro-antd/icon";


@NgModule({
  declarations: [
    ModuleApiConfigComponent,
    ModuleManagementComponent,
  ],
  imports: [
    CommonModule,
    NzButtonModule,
    NzTreeModule,
    NzGridModule,
    NzIconModule
  ],
  exports: [
    ModuleApiConfigComponent
  ]
})
export class ModuleApiConfigModule {
}
