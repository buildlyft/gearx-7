import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { MachineOperatorService } from '../service/machineOperator.service';
import { MachineOperatorDeleteDialogComponent } from './machineOperator-delete-dialog.component';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

describe('MachineOperatorDeleteDialogComponent', () => {
  let comp: MachineOperatorDeleteDialogComponent;
  let fixture: ComponentFixture<MachineOperatorDeleteDialogComponent>;
  let service: MachineOperatorService;
  let activeModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MachineOperatorDeleteDialogComponent],
      providers: [NgbActiveModal],
    }).compileComponents();

    fixture = TestBed.createComponent(MachineOperatorDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MachineOperatorService);
    activeModal = TestBed.inject(NgbActiveModal);
  });

  it('Should call delete service on confirmDelete', fakeAsync(() => {
    jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({})));

    comp.confirmDelete(123);
    tick();

    expect(service.delete).toHaveBeenCalledWith(123);
    expect(activeModal.close).toHaveBeenCalledWith(ITEM_DELETED_EVENT);
  }));

  it('Should dismiss modal on cancel', () => {
    jest.spyOn(activeModal, 'dismiss');

    comp.cancel();

    expect(activeModal.dismiss).toHaveBeenCalled();
  });
});
