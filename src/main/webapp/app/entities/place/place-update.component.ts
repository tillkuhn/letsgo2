import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { IPlace, Place } from 'app/shared/model/place.model';
import { PlaceService } from './place.service';

@Component({
  selector: 'jhi-place-update',
  templateUrl: './place-update.component.html'
})
export class PlaceUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    summary: [],
    imageUrl: [],
    rating: [],
    lotype: [],
    country: [],
    updatedBy: [],
    coordinates: [],
    notes: [],
    updatedAt: [],
    primaryUrl: []
  });

  constructor(protected placeService: PlaceService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ place }) => {
      this.updateForm(place);
    });
  }

  updateForm(place: IPlace) {
    this.editForm.patchValue({
      id: place.id,
      name: place.name,
      summary: place.summary,
      imageUrl: place.imageUrl,
      rating: place.rating,
      lotype: place.lotype,
      country: place.country,
      updatedBy: place.updatedBy,
      coordinates: place.coordinates,
      notes: place.notes,
      updatedAt: place.updatedAt != null ? place.updatedAt.format(DATE_TIME_FORMAT) : null,
      primaryUrl: place.primaryUrl
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const place = this.createFromForm();
    if (place.id !== undefined) {
      this.subscribeToSaveResponse(this.placeService.update(place));
    } else {
      this.subscribeToSaveResponse(this.placeService.create(place));
    }
  }

  private createFromForm(): IPlace {
    return {
      ...new Place(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      summary: this.editForm.get(['summary']).value,
      imageUrl: this.editForm.get(['imageUrl']).value,
      rating: this.editForm.get(['rating']).value,
      lotype: this.editForm.get(['lotype']).value,
      country: this.editForm.get(['country']).value,
      updatedBy: this.editForm.get(['updatedBy']).value,
      coordinates: this.editForm.get(['coordinates']).value,
      notes: this.editForm.get(['notes']).value,
      updatedAt:
        this.editForm.get(['updatedAt']).value != null ? moment(this.editForm.get(['updatedAt']).value, DATE_TIME_FORMAT) : undefined,
      primaryUrl: this.editForm.get(['primaryUrl']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlace>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
