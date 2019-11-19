import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'place',
        loadChildren: () => import('./place/place.module').then(m => m.Letsgo2PlaceModule)
      },
      {
        path: 'region',
        loadChildren: () => import('./region/region.module').then(m => m.Letsgo2RegionModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class Letsgo2EntityModule {}
