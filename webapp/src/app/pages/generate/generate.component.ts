import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest, HttpResponse} from "@angular/common/http";
import {downloadFromResponse} from "../../utils/http-download";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {NzUploadFile} from "ng-zorro-antd/upload";
import {filter} from "rxjs";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-generate',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.less']
})
export class GenerateComponent implements OnInit {
  schemaList: string[] = [];
  fileList: NzUploadFile[] = [];

  loading = false;
  validateForm: FormGroup;
  uploadAccept = '.ftl';


  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.validateForm = this.fb.group({
      datasourceId: [null, [Validators.required]],
      dbSchema: [null,],
      tableNames: [[],],
      title: ['数据库表结构文档',],
      description: ['数据库表结构文档',],
      version: ['1.0.0',],
      fileType: ['HTML',],
      produceType: ['freemarker',],
    })
  }

  ngOnInit(): void {
  }

  beforeUpload = (file: NzUploadFile): boolean => {
    this.fileList = [file];
    return false;
  };


  downloadDefaultTemplate() {
    window.open(`${environment.apiPrefix}/api/document/download-templates`);
  }

  submit() {
    const config = this.validateForm.getRawValue();
    const data = new FormData();
    data.append('json', JSON.stringify(config))
    if (this.fileList.length > 0) {
      data.append('template', this.fileList[0] as any);
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