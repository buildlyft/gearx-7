import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { VehicleDocumentComponent } from './vehicleDocument.component';
import { VehicleDocumentService } from '../service/vehicleDocument.service';
import { AccountService } from 'app/core/auth/account.service';
import { IVehicleDocumentResponse } from '../vehicleDocument.model';
import { VehicleDocumentDeleteDialogComponent } from '../delete/vehicleDocument-delete-dialog.component';

describe('VehicleDocumentComponent', () => {
  let comp: VehicleDocumentComponent;
  let fixture: ComponentFixture<VehicleDocumentComponent>;
  let service: jasmine.SpyObj<VehicleDocumentService>;
  let accountService: jasmine.SpyObj<AccountService>;
  let modalService: jasmine.SpyObj<NgbModal>;

  beforeEach(async () => {
    const serviceSpy = jasmine.createSpyObj('VehicleDocumentService', ['getAllDocuments', 'deleteDocument']);

    const accountSpy = jasmine.createSpyObj('AccountService', ['getAuthenticationState']);

    const modalSpy = jasmine.createSpyObj('NgbModal', ['open']);

    await TestBed.configureTestingModule({
      declarations: [VehicleDocumentComponent],
      providers: [
        { provide: VehicleDocumentService, useValue: serviceSpy },
        { provide: AccountService, useValue: accountSpy },
        { provide: NgbModal, useValue: modalSpy },
      ],
    })
      .overrideTemplate(VehicleDocumentComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VehicleDocumentComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(VehicleDocumentService) as jasmine.SpyObj<VehicleDocumentService>;
    accountService = TestBed.inject(AccountService) as jasmine.SpyObj<AccountService>;
    modalService = TestBed.inject(NgbModal) as jasmine.SpyObj<NgbModal>;
  });

  describe('ngOnInit', () => {
    it('should load all documents on init', () => {
      const response: IVehicleDocumentResponse[] = [{ machineId: 1, documents: [{ id: 123, fileName: 'test.pdf' }] as any }];

      accountService.getAuthenticationState.and.returnValue(of({ authorities: ['ROLE_ADMIN'] } as any));
      service.getAllDocuments.and.returnValue(of(response));

      comp.ngOnInit();

      expect(service.getAllDocuments).toHaveBeenCalled();
      expect(comp.documents.length).toBe(1);
      expect(comp.isAdminOrPartner).toBeTrue();
    });
  });

  describe('delete', () => {
    it('should open delete dialog and reload list', () => {
      const modalClosed = new Subject<string>();
      const mockModalRef = {
        componentInstance: {},
        closed: modalClosed.asObservable(),
      };

      modalService.open.and.returnValue(mockModalRef as any);
      spyOn(comp, 'loadAll');

      comp.delete(123);

      expect(modalService.open).toHaveBeenCalledWith(
        VehicleDocumentDeleteDialogComponent,
        jasmine.objectContaining({
          size: 'lg',
          backdrop: 'static',
        }),
      );

      modalClosed.next('deleted');
      modalClosed.complete();

      expect(comp.loadAll).toHaveBeenCalled();
    });
  });

  describe('trackId', () => {
    it('should return document id', () => {
      const result = comp.trackId(0, { id: 999 } as any);
      expect(result).toBe(999);
    });
  });
});
