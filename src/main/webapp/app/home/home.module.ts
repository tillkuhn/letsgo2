import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Letsgo2SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';

@NgModule({
  imports: [Letsgo2SharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent]
})
export class Letsgo2HomeModule {}
