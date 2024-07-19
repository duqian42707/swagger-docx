export function readTextFile(file: any): Promise<string | null> {
  return new Promise<string | null>(resolve => {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      resolve(e.target.result);
    };
    reader.onerror = function () {
      console.log('读取失败', reader.error);
      resolve(null);
    };
    reader.readAsText(file, 'utf-8');
  })
}
