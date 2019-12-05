import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { PlaceService } from 'app/entities/place/place.service';
import { IPlace, Place } from 'app/shared/model/place.model';

describe('Service Tests', () => {
  describe('Place Service', () => {
    let injector: TestBed;
    let service: PlaceService;
    let httpMock: HttpTestingController;
    let elemDefault: IPlace;
    let expectedResult;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(PlaceService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Place(
        "0",
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        0,
        'AAAAAAA',
        'AAAAAAA',
        [1.2,3.4],
        'AAAAAAA',
        'AAAAAAA',
        currentDate,
        'AAAAAAA'
      );
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            updatedAt: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        service
          .find("123")
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a Place', () => {
        const returnedFromService = Object.assign(
          {
            id: "0",
            updatedAt: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            updatedAt: currentDate
          },
          returnedFromService
        );
        service
          .create(new Place(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a Place', () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            summary: 'BBBBBB',
            imageUrl: 'BBBBBB',
            rating: 1,
            lotype: 'BBBBBB',
            country: 'BBBBBB',
            updatedBy: 'BBBBBB',
            coordinates: 'BBBBBB',
            notes: 'BBBBBB',
            updatedAt: currentDate.format(DATE_TIME_FORMAT),
            primaryUrl: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            updatedAt: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of Place', () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            summary: 'BBBBBB',
            imageUrl: 'BBBBBB',
            rating: 1,
            lotype: 'BBBBBB',
            country: 'BBBBBB',
            updatedBy: 'BBBBBB',
            coordinates: 'BBBBBB',
            notes: 'BBBBBB',
            updatedAt: currentDate.format(DATE_TIME_FORMAT),
            primaryUrl: 'BBBBBB'
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            updatedAt: currentDate
          },
          returnedFromService
        );
        service
          .query(expected)
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Place', () => {
        service.delete("123").subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
