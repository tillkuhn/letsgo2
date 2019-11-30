import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Letsgo2SharedModule } from 'app/shared/shared.module';
import { MAP_ROUTE } from './map.route';
import { MapComponent } from './map.component';
import { NgxMapboxGLModule } from 'ngx-mapbox-gl';

@NgModule({
  imports: [
    Letsgo2SharedModule,
    RouterModule.forChild([MAP_ROUTE]),
    NgxMapboxGLModule
  ],
  declarations: [MapComponent]
})
export class Letsgo2MapModule {}
