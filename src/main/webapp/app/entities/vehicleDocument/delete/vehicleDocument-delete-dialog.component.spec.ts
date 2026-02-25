import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';

import { VehicleDocumentDeleteDialogComponent } from './vehicleDocument-delete-dialog.component';
import { VehicleDocumentService } from '../service/vehicleDocument.service';

describe('VehicleDocumentDeleteDialogComponent', () => {
  let comp: VehicleDocumentDeleteDialogComponent;
  let fixture: ComponentFixture<VehicleDocumentDeleteDialogComponent>;
  let service: jasmine.SpyObj<VehicleDocumentService>;
  let activeModal: NgbActiveModal;

  beforeEach(async () => {
    const serviceSpy = jasmine.createSpyObj('VehicleDocumentService', ['deleteDocument']);

    await TestBed.configureTestingModule({
      declarations: [VehicleDocumentDeleteDialogComponent],
      providers: [{ provide: VehicleDocumentService, useValue: serviceSpy }, NgbActiveModal],
    })
      .overrideTemplate(VehicleDocumentDeleteDialogComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VehicleDocumentDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(VehicleDocumentService) as jasmine.SpyObj<VehicleDocumentService>;
    activeModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('should call deleteDocument service on confirmDelete', () => {
      // GIVEN
      const id = 123;
      comp.id = id;
      service.deleteDocument.and.returnValue(of({}));

      spyOn(activeModal, 'close');

      // WHEN
      comp.confirmDelete();

      // THEN
      expect(service.deleteDocument).toHaveBeenCalledWith(id);
      expect(activeModal.close).toHaveBeenCalledWith('deleted');
    });
  });

  describe('cancel', () => {
    it('should dismiss the modal on cancel', () => {
      // GIVEN
      spyOn(activeModal, 'dismiss');

      // WHEN
      comp.cancel();

      // THEN
      expect(activeModal.dismiss).toHaveBeenCalled();
    });
  });
});
