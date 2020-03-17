import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { map, bufferTime, filter } from "rxjs/operators";

@Injectable({
  providedIn: "root"
})
export class ProductsService {
  eventSource: EventSource;
  constructor() {}

  fetchProducts(): Observable<any> {
    this.eventSource = new EventSource("http://localhost:9003/products/sse", {
      withCredentials: true
    });

    const subject: Subject<any> = new Subject();

    this.eventSource.addEventListener('poison', e => {
      console.log('Received poison, closing');
      this.eventSource.close();
      subject.complete();
    });

    this.eventSource.onmessage = (msg: MessageEvent) => {
      subject.next(msg.data);
    };

    return subject.pipe(
      map(v => JSON.parse(v)),
      bufferTime(500),
      filter(buffer => buffer.length > 0)
    );
  }
}
