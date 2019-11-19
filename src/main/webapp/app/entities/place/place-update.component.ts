import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
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
    name: [null, [Validators.required, Validators.minLength(3)]],
    summary: []
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
      summary: place.summary
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
      summary: this.editForm.get(['summary']).value
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
