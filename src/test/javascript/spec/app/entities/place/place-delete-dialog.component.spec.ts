import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { Letsgo2TestModule } from '../../../test.module';
import { PlaceDeleteDialogComponent } from 'app/entities/place/place-delete-dialog.component';
import { PlaceService } from 'app/entities/place/place.service';

describe('Component Tests', () => {
  describe('Place Management Delete Component', () => {
    let comp: PlaceDeleteDialogComponent;
    let fixture: ComponentFixture<PlaceDeleteDialogComponent>;
    let service: PlaceService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [Letsgo2TestModule],
        declarations: [PlaceDeleteDialogComponent]
      })
        .overrideTemplate(PlaceDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PlaceDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PlaceService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete("123");
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith("123");
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
