import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IPoi } from 'app/shared/model/poi.model';
type EntityArrayResponseType = HttpResponse<IPoi[]>;

@Injectable({ providedIn: 'root' })
export class PoiService {
  public resourceUrl = SERVER_API_URL + 'api/coordinates';

  constructor(protected http: HttpClient) {}

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPoi[]>(this.resourceUrl, { params: options, observe: 'response' });
  }
}
