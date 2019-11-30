import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { Letsgo2SharedModule } from 'app/shared/shared.module';
import { Letsgo2CoreModule } from 'app/core/core.module';
import { Letsgo2AppRoutingModule } from './app-routing.module';
import { Letsgo2HomeModule } from './home/home.module';
import { Letsgo2EntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { JhiMainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';
import {Letsgo2MapModule} from "./map/map.module";

@NgModule({
  imports: [
    BrowserModule,
    Letsgo2SharedModule,
    Letsgo2CoreModule,
    Letsgo2HomeModule,
    Letsgo2MapModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    Letsgo2EntityModule,
    Letsgo2AppRoutingModule
  ],
  declarations: [JhiMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  bootstrap: [JhiMainComponent]
})
export class Letsgo2AppModule {}
