import { Component, OnInit } from "@angular/core";
import { Product } from '../product';
import { Observable, Subject, BehaviorSubject } from 'rxjs'

@Component({
  selector: "app-products",
  templateUrl: "./products.component.html",
  styleUrls: ["./products.component.css"]
})
export class ProductsComponent implements OnInit {

  eventSource: EventSource;
  productSubject: Subject<Product[]> = new Subject<Product[]>();
  productSubject$: Observable<Product[]> = this.productSubject.asObservable();
  products: Product[] = [];

  constructor() {}

  ngOnInit() {

    this.eventSource = new EventSource('//localhost:9003/products/sse', { withCredentials: true });
    const self = this;

    this.eventSource.onopen = () => {
      console.log("opened event source");
    }

    this.eventSource.onerror = (err) => {
      this.eventSource.close();
    }

    this.eventSource.onmessage = (event) => {
      if(event.data == "poisonPill") {
        this.eventSource.close();
      } else {
        const obj = JSON.parse(event.data) as Product;
        self.products.push(obj);
        self.productSubject.next(self.products);
      }

    };
  }
}
