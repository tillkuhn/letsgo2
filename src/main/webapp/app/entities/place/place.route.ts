import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Place } from 'app/shared/model/place.model';
import { PlaceService } from './place.service';
import { PlaceComponent } from './place.component';
import { PlaceDetailComponent } from './place-detail.component';
import { PlaceUpdateComponent } from './place-update.component';
import { PlaceDeletePopupComponent } from './place-delete-dialog.component';
import { IPlace } from 'app/shared/model/place.model';

@Injectable({ providedIn: 'root' })
export class PlaceResolve implements Resolve<IPlace> {
  constructor(private service: PlaceService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPlace> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((place: HttpResponse<Place>) => place.body));
    }
    return of(new Place());
  }
}

export const placeRoute: Routes = [
  {
    path: '',
    component: PlaceComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'letsgo2App.place.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PlaceDetailComponent,
    resolve: {
      place: PlaceResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'letsgo2App.place.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PlaceUpdateComponent,
    resolve: {
      place: PlaceResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'letsgo2App.place.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PlaceUpdateComponent,
    resolve: {
      place: PlaceResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'letsgo2App.place.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const placePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: PlaceDeletePopupComponent,
    resolve: {
      place: PlaceResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'letsgo2App.place.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
