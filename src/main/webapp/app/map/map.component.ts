import {Component, OnInit} from '@angular/core';
import {MAPBOX_GL_ACCESS_TOKEN} from 'app/app.constants';
import {MapboxGeoJSONFeature, MapLayerMouseEvent} from 'mapbox-gl';

// check https://medium.com/@timo.baehr/using-mapbox-in-angular-application-bc3b2b38592
@Component({
  selector: 'jhi-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {
  // The latitude of Bangkok, Thailand is 13.736717, and the longitude is 100.523186.
  coordinates = [100.523186, 13.736717];
  zoom = 5;
  accessToken = MAPBOX_GL_ACCESS_TOKEN;
  points: GeoJSON.FeatureCollection<GeoJSON.Point>;
  selectedPoint: MapboxGeoJSONFeature | null;
  cursorStyle: string;

  ngOnInit() {
    this.points = {
      'type': 'FeatureCollection',
      'features': [{
        'type': 'Feature',
        'properties': {
          // tslint:disable-next-line:max-line-length
          'description': '<strong>Make it to Bangkok</strong><p><a href="http://www.mtpleasantdc.com/makeitmtpleasant" target="_blank" title="Opens in a new window">Make it Mount Pleasant</a> is a handmade and vintage market and afternoon of live entertainment and kids activities. 12:00-6:00 p.m.</p>',
          'icon': 'theatre'
        },
        'geometry': {
          'type': 'Point',
          'coordinates': [100.523186, 13.736717]
        }
      }, {
        'type': 'Feature',
        'properties': {
          // tslint:disable-next-line:max-line-length
          'description': '<strong>Rayong is the Gateway to Ko Samet</strong><p>Head to Lounge 201 (201 Massachusetts Avenue NE) Sunday for a <a href="http://madmens5finale.eventbrite.com/" target="_blank" title="Opens in a new window">Mad Men Season Five Finale Watch Party</a>, complete with 60s costume contest, Mad Men trivia, and retro food and drink. 8:00-11:00 p.m. $10 general admission, $20 admission and two hour open bar.</p>',
          'icon': 'theatre'
        },
        'geometry': {
          'type': 'Point',
          'coordinates': [101.523186, 12.736717]
        }
      }]
    };
  }

  onClick(evt: MapLayerMouseEvent) {
    // this.selectedPoint = evt.features![0];
    // 50:26  error    This assertion is unnecessary ... typescript-eslint/no-unnecessary-type-assertion ß?
    this.selectedPoint = evt.features[0];
  }

}

