import { Component, OnInit } from "@angular/core";
import { Observable } from "rxjs";
import {ProductsService} from "../products.service";

@Component({
  selector: "app-products",
  templateUrl: "./products.component.html",
  styleUrls: ["./products.component.css"]
})
export class ProductsComponent implements OnInit {
  products$: Observable<any[]>;
  products: any[] = [];
  searchString: string;

  constructor(private service: ProductsService) {}

  ngOnInit() {
    const self = this;


    this.products$ = new Observable(obs => {
      const source = self.service.fetchProducts();

      source.subscribe(
        val => {
          console.log(val);
          this.products = [ ... this.products, ... val ];
          obs.next(this.products);
        },
        err => console.log(err),
        () => obs.complete()
      );
    });
  }
}
