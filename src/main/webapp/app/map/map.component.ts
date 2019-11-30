import { Component, OnInit } from '@angular/core';
import { MAPBOX_GL_ACCESS_TOKEN } from 'app/app.constants';
// check https://medium.com/@timo.baehr/using-mapbox-in-angular-application-bc3b2b38592
@Component({
  selector: 'jhi-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {
  // The latitude of Bangkok, Thailand is 13.736717, and the longitude is 100.523186.
  coordinates = [100.523186,13.736717];
  zoom =5;
  accessToken= MAPBOX_GL_ACCESS_TOKEN;


}
