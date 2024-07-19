import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest, HttpResponse} from "@angular/common/http";
import {downloadFromResponse} from "../../utils/http-download";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {NzUploadFile} from "ng-zorro-antd/upload";
import {filter} from "rxjs";
import {environment} from "../../../environments/environment";
import {NzMessageService} from "ng-zorro-antd/message";
import {readTextFile} from "../../utils/read-text";

@Component({
  selector: 'app-generate',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.less']
})
export class GenerateComponent implements OnInit {
  swaggerFileList: NzUploadFile[] = [];
  templateFileList: NzUploadFile[] = [];

  loading = false;
  validateForm: FormGroup;


  constructor(private http: HttpClient, private fb: FormBuilder, private msg: NzMessageService) {
    this.validateForm = this.fb.group({
      moduleList: [null,],
      swaggerJson: [null]
    })
  }

  ngOnInit(): void {
  }

  beforeUpload1 = (file: NzUploadFile): boolean => {
    this.swaggerFileList = [file];
    readTextFile(file).then(res => {
      this.validateForm.patchValue({swaggerJson: res})
    })
    return false;
  };

  beforeUpload2 = (file: NzUploadFile): boolean => {
    this.templateFileList = [file];
    return false;
  };


  downloadDefaultTemplate() {
    window.open(`${environment.apiPrefix}/api/document/download-templates`);
  }

  submit() {
    const formData = this.validateForm.getRawValue();
    const generateParam = {
      moduleList: eval(formData.moduleList)
    }
    const data = new FormData();
    data.append('swaggerJson', formData.swaggerJson)
    data.append('paramJson', JSON.stringify(generateParam))
    if (this.swaggerFileList.length == 0) {
      this.msg.warning('请上传Swagger Json文件！');
      return;
    }
    if (this.templateFileList.length == 0) {
      this.msg.warning('请上传word模板文件！');
      return;
    }
    for (const file of this.swaggerFileList) {
      data.append('swaggerFiles', file as any);
    }
    if (this.templateFileList.length > 0) {
      data.append('templateFile', this.templateFileList[0] as any);
    }
    this.loading = true;

    const req = new HttpRequest('POST', `${environment.apiPrefix}/api/document/generate`, data, {
      reportProgress: true,
      responseType: 'blob',
    });

    this.http
      .request(req)
      .pipe(filter(e => e instanceof HttpResponse))
      .subscribe({
        next: (resp: HttpEvent<unknown>) => {
          this.loading = false;
          downloadFromResponse(resp as any);
        },
        error: error => {
          this.loading = false;
        }
      })
  }
}
