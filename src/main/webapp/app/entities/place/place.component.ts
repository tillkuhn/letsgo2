import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager } from 'ng-jhipster';

import { IPlace } from 'app/shared/model/place.model';
import { AccountService } from 'app/core/auth/account.service';
import { PlaceService } from './place.service';

@Component({
  selector: 'jhi-place',
  templateUrl: './place.component.html'
})
export class PlaceComponent implements OnInit, OnDestroy {
  places: IPlace[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(protected placeService: PlaceService, protected eventManager: JhiEventManager, protected accountService: AccountService) {}

  refresh() {
    this.loadAll();
  }

  loadAll() {
    this.placeService
      .query()
      .pipe(
        filter((res: HttpResponse<IPlace[]>) => res.ok),
        map((res: HttpResponse<IPlace[]>) => res.body)
      )
      .subscribe((res: IPlace[]) => {
        this.places = res;
      });
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().subscribe(account => {
      this.currentAccount = account;
    });
    this.registerChangeInPlaces();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IPlace) {
    return item.id;
  }

  registerChangeInPlaces() {
    this.eventSubscriber = this.eventManager.subscribe('placeListModification', response => this.loadAll());
  }
}
