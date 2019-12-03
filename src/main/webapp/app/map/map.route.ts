import { Route } from '@angular/router';

import { MapComponent } from './map.component';

export const MAP_ROUTE: Route = {
  path: 'map',
  component: MapComponent,
  data: {
    authorities: [],
    pageTitle: 'map.title'
  }
};
