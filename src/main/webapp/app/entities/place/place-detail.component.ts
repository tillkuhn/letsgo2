import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPlace } from 'app/shared/model/place.model';

@Component({
  selector: 'jhi-place-detail',
  templateUrl: './place-detail.component.html'
})
export class PlaceDetailComponent implements OnInit {
  place: IPlace;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ place }) => {
      this.place = place;
    });
  }

  previousState() {
    window.history.back();
  }
}
