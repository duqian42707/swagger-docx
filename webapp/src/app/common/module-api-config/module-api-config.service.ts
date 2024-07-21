import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ModuleApiConfigService {

  constructor() {
  }


  extractApiList(swaggerData: any): any[] {
    const apiList = [];
    for (const path in swaggerData.paths) {
      const pathData = swaggerData.paths[path]
      for (const method in pathData) {
        const methodData = pathData[method];
        apiList.push({...methodData, path: path, method: method})
      }
    }
    return apiList;
  }

  handleTreeData(swaggerData: any): any[] {
    const apiList = this.extractApiList(swaggerData);
    return swaggerData.tags.map((item: any) => {
      const children = this.getChildren(apiList, item.name);
      return {key: item.name, title: item.name, children: children}
    });
  }

  getChildren(apiList: any[], tag: string) {
    return apiList.filter(api => api.deprecated !== true && api.tags.includes(tag)).map(api => {
      return {...api, key: `${api.method}-${api.path}`, title: `${api.summary}-${api.method}-${api.path}`, isLeaf: true}
    })
  }
}
