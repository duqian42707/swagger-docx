import {Component, forwardRef, OnInit} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {NzModalService} from "ng-zorro-antd/modal";
import {NzUploadFile} from "ng-zorro-antd/upload";
import {readTextFile} from "../../utils/read-text";
import {NzUploadChangeParam} from "ng-zorro-antd/upload/interface";

@Component({
  selector: 'app-swagger-file-config',
  templateUrl: './swagger-file-config.component.html',
  styleUrls: ['./swagger-file-config.component.less'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => SwaggerFileConfigComponent),
    multi: true
  }]
})
export class SwaggerFileConfigComponent implements OnInit, ControlValueAccessor {

  swaggerJsons: string[] = [];
  private innerValue: string[] = [];
  private onTouchedCallback: any = () => void 0;
  private onChangeCallback: any = () => void 0;
  disabled = false;

  swaggerFileList: NzUploadFile[] = [];
  uidContentList: Array<{ uid: string, content: string }> = [];


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


  beforeUpload = (file: NzUploadFile): boolean => {
    this.swaggerFileList = [...this.swaggerFileList, file];
    readTextFile(file).then(res => {
      if (res) {
        this.uidContentList.push({uid: file.uid, content: res});
        this.value = this.uidContentList.map(x => x.content);
      }
    })
    return false;
  };

  constructor(private http: HttpClient, private modalService: NzModalService) {
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

  fileListChange(evt: NzUploadChangeParam) {
    if (evt.type == 'removed') {
      this.uidContentList = this.uidContentList.filter(x => x.uid !== evt.file.uid);
      this.value = this.uidContentList.map(x => x.content);
    }
  }
}
