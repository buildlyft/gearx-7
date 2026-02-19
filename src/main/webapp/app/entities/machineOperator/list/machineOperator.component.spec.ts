import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of, Subject } from 'rxjs';

import { MachineOperatorComponent } from './machineOperator.component';
import { MachineOperatorService } from '../service/machineOperator.service';
import { MachineOperatorDeleteDialogComponent } from '../delete/machineOperator-delete-dialog.component';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

describe('MachineOperatorComponent', () => {
  let comp: MachineOperatorComponent;
  let fixture: ComponentFixture<MachineOperatorComponent>;
  let service: MachineOperatorService;
  let modalService: NgbModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MachineOperatorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MachineOperatorComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MachineOperatorService);
    modalService = TestBed.inject(NgbModal);
  });

  describe('ngOnInit', () => {
    it('Should call load on init', () => {
      const mockResponse = new HttpResponse({
        body: [
          {
            operatorId: 1,
            machineId: 101,
            driverName: 'Ravi',
          },
        ],
      });

      jest.spyOn(service, 'getAllActive').mockReturnValue(of(mockResponse));

      comp.ngOnInit();

      expect(service.getAllActive).toHaveBeenCalled();
      expect(comp.machineOperators).toEqual(mockResponse.body);
    });
  });

  describe('delete', () => {
    it('Should open delete dialog and reload on confirm', () => {
      const modalClosed = new Subject<string>();
      const mockModalRef = {
        componentInstance: {},
        closed: modalClosed.asObservable(),
      };

      jest.spyOn(modalService, 'open').mockReturnValue(mockModalRef as any);
      jest.spyOn(comp, 'load');

      const operator = { operatorId: 1 } as any;

      comp.delete(operator);

      expect(modalService.open).toHaveBeenCalledWith(
        MachineOperatorDeleteDialogComponent,
        expect.objectContaining({
          size: 'lg',
          backdrop: 'static',
        }),
      );

      modalClosed.next(ITEM_DELETED_EVENT);
      modalClosed.complete();

      expect(comp.load).toHaveBeenCalled();
    });
  });
});
