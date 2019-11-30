import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPlace } from 'app/shared/model/place.model';
import { PlaceService } from './place.service';

@Component({
  selector: 'jhi-place-delete-dialog',
  templateUrl: './place-delete-dialog.component.html'
})
export class PlaceDeleteDialogComponent {
  place: IPlace;

  constructor(protected placeService: PlaceService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.placeService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'placeListModification',
        content: 'Deleted an place'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-place-delete-popup',
  template: ''
})
export class PlaceDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ place }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(PlaceDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.place = place;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/place', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/place', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
