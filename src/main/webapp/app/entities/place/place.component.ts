import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { IPlace } from 'app/shared/model/place.model';
import { PlaceService } from './place.service';

@Component({
  selector: 'jhi-place',
  templateUrl: './place.component.html'
})
export class PlaceComponent implements OnInit, OnDestroy {
  places: IPlace[];
  eventSubscriber: Subscription;

  constructor(protected placeService: PlaceService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.placeService.query().subscribe((res: HttpResponse<IPlace[]>) => {
      this.places = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInPlaces();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IPlace) {
    return item.id;
  }

  registerChangeInPlaces() {
    this.eventSubscriber = this.eventManager.subscribe('placeListModification', () => this.loadAll());
  }
}
