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
  searchString: string;

  constructor(private service: ProductsService) {}

  ngOnInit() {
    const self = this;
    let array = [];


    this.products$ = new Observable(obs => {
      const source = self.service.fetchProducts();

      source.subscribe(
        val => {
          array.push(val);
        },
        err => console.log(err),
        () => obs.complete()
      );

      const interval = setInterval(() => {
        obs.next(array);
      }, 100);
    });
  }
}
