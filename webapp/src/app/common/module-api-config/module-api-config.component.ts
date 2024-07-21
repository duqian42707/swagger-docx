import {Component, forwardRef, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {NzModalService} from "ng-zorro-antd/modal";
import {ModuleApiConfigService} from "./module-api-config.service";

@Component({
  selector: 'app-module-api-config',
  templateUrl: './module-api-config.component.html',
  styleUrls: ['./module-api-config.component.less'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => ModuleApiConfigComponent),
    multi: true
  }]
})
export class ModuleApiConfigComponent implements OnInit, ControlValueAccessor {

  @Input() swaggerJsons: string[] = [];
  private innerValue: string[] = [];
  private onTouchedCallback: any = () => void 0;
  private onChangeCallback: any = () => void 0;
  disabled = false;

  treeData: any = [];

  get selectedCount() {
    return this.value.length;
  }

  get value(): string[] {
    console.debug('get value:', this.innerValue);
    return this.innerValue;
  }

  set value(v: string[]) {
    console.debug('set value:', v);
    if (v !== this.innerValue) {
      this.innerValue = v;
      this.onChangeCallback(v);
    }
  }

  @ViewChild("modalContentTpl") modalContentTpl!: TemplateRef<any>;

  constructor(private http: HttpClient, private modalService: NzModalService, private service: ModuleApiConfigService) {
  }

  ngOnInit(): void {
  }

  // ------------------------------------------------------------------------
  // | Control value accessor implements
  // | 参考：https://www.jianshu.com/p/6d5a4e6af0c1
  // ------------------------------------------------------------------------

  registerOnChange(fn: any): void {
    this.onChangeCallback = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouchedCallback = fn;
  }

  writeValue(obj: any): void {
    console.debug('write value:', obj);
    if (this.innerValue !== obj) {
      this.innerValue = obj;
    }
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  // Control value accessor implements end.

  openConfigModal() {
    let treeData: any = [];
    for (let i = 0; i < this.swaggerJsons.length; i++) {
      const list = this.service.handleTreeData(JSON.parse(this.swaggerJsons[i]));
      treeData.push({
        key: 'api-' + (i + 1),
        title: 'Swagger文档' + (i + 1),
        children: list
      })
    }
    this.treeData = treeData;
    this.modalService.create({
      nzWidth: '1000px',
      nzTitle: '模块接口配置',
      nzContent: this.modalContentTpl,
      nzOnOk: () => {
        console.log('ok');
      }
    })
  }

}
