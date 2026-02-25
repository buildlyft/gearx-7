import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { VehicleDocumentComponent } from './vehicleDocument.component';
import { VehicleDocumentService } from '../service/vehicleDocument.service';
import { AccountService } from 'app/core/auth/account.service';
import { IVehicleDocumentResponse } from '../vehicleDocument.model';

describe('VehicleDocumentComponent', () => {
  let comp: VehicleDocumentComponent;
  let fixture: ComponentFixture<VehicleDocumentComponent>;
  let service: jasmine.SpyObj<VehicleDocumentService>;
  let accountService: jasmine.SpyObj<AccountService>;
  let modalService: jasmine.SpyObj<NgbModal>;
  let queryParamsSubject: Subject<any>;

  beforeEach(async () => {
    const serviceSpy = jasmine.createSpyObj('VehicleDocumentService', ['getMachineDocuments', 'uploadDocuments', 'deleteDocument']);

    const accountSpy = jasmine.createSpyObj('AccountService', ['getAuthenticationState']);

    const modalSpy = jasmine.createSpyObj('NgbModal', ['open']);

    queryParamsSubject = new Subject();

    await TestBed.configureTestingModule({
      declarations: [VehicleDocumentComponent],
      providers: [
        { provide: VehicleDocumentService, useValue: serviceSpy },
        { provide: AccountService, useValue: accountSpy },
        { provide: NgbModal, useValue: modalSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: queryParamsSubject.asObservable(),
          },
        },
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
    it('should load documents when machineId is present in query params', () => {
      // GIVEN
      const response: IVehicleDocumentResponse = {
        machineId: 1,
        documents: [{ id: 123, fileName: 'test.pdf' }],
      };

      accountService.getAuthenticationState.and.returnValue(of({ authorities: ['ROLE_ADMIN'] } as any));
      service.getMachineDocuments.and.returnValue(of(response));

      // WHEN
      comp.ngOnInit();
      queryParamsSubject.next({ machineId: 1 });

      // THEN
      expect(comp.machineId).toBe(1);
      expect(service.getMachineDocuments).toHaveBeenCalledWith(1);
      expect(comp.documents.length).toBe(1);
      expect(comp.isAdminOrPartner).toBeTrue();
    });

    it('should set role false when user is not ADMIN or PARTNER', () => {
      // GIVEN
      accountService.getAuthenticationState.and.returnValue(of({ authorities: ['ROLE_USER'] } as any));

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.isAdminOrPartner).toBeFalse();
    });
  });

  describe('loadDocuments', () => {
    it('should call service and load documents', () => {
      // GIVEN
      const response: IVehicleDocumentResponse = {
        machineId: 5,
        documents: [{ id: 1, fileName: 'doc1.pdf' }],
      };

      comp.machineId = 5;
      service.getMachineDocuments.and.returnValue(of(response));

      // WHEN
      comp.loadDocuments();

      // THEN
      expect(service.getMachineDocuments).toHaveBeenCalledWith(5);
      expect(comp.documents).toEqual(response.documents!);
      expect(comp.isLoading).toBeFalse();
    });

    it('should not call service if machineId is null', () => {
      // GIVEN
      comp.machineId = null;

      // WHEN
      comp.loadDocuments();

      // THEN
      expect(service.getMachineDocuments).not.toHaveBeenCalled();
    });
  });

  describe('uploadDocuments', () => {
    it('should upload files and reload documents', () => {
      // GIVEN
      comp.machineId = 10;
      comp.selectedFiles = [new File(['test'], 'test.pdf')];

      service.uploadDocuments.and.returnValue(of({ machineId: 10, documents: [] }));
      spyOn(comp, 'loadDocuments');

      // WHEN
      comp.uploadDocuments();

      // THEN
      expect(service.uploadDocuments).toHaveBeenCalled();
      expect(comp.selectedFiles.length).toBe(0);
      expect(comp.loadDocuments).toHaveBeenCalled();
    });

    it('should not upload if no files selected', () => {
      // GIVEN
      comp.machineId = 10;
      comp.selectedFiles = [];

      // WHEN
      comp.uploadDocuments();

      // THEN
      expect(service.uploadDocuments).not.toHaveBeenCalled();
    });
  });

  describe('previewFile', () => {
    it('should set image preview type for image files', () => {
      // WHEN
      comp.previewFile({
        fileUrl: 'image.png',
        contentType: 'image/png',
      } as any);

      // THEN
      expect(comp.previewType).toBe('image');
      expect(comp.previewUrl).toBe('image.png');
    });

    it('should set pdf preview type for pdf files', () => {
      // WHEN
      comp.previewFile({
        fileUrl: 'file.pdf',
        contentType: 'application/pdf',
      } as any);

      // THEN
      expect(comp.previewType).toBe('pdf');
      expect(comp.previewUrl).toBe('file.pdf');
    });

    it('should set other preview type for unsupported files', () => {
      // WHEN
      comp.previewFile({
        fileUrl: 'file.zip',
        contentType: 'application/zip',
      } as any);

      // THEN
      expect(comp.previewType).toBe('other');
      expect(comp.previewUrl).toBe('file.zip');
    });
  });

  describe('closePreview', () => {
    it('should reset preview values', () => {
      // GIVEN
      comp.previewUrl = 'test.pdf';
      comp.previewType = 'pdf';

      // WHEN
      comp.closePreview();

      // THEN
      expect(comp.previewUrl).toBeNull();
      expect(comp.previewType).toBeNull();
    });
  });

  describe('delete', () => {
    it('should open delete dialog modal', () => {
      // GIVEN
      const modalRef: any = {
        componentInstance: {},
        closed: of('deleted'),
      };
      modalService.open.and.returnValue(modalRef);
      spyOn(comp, 'loadDocuments');

      // WHEN
      comp.delete(123);

      // THEN
      expect(modalService.open).toHaveBeenCalled();
      expect(modalRef.componentInstance.id).toBe(123);
    });
  });

  describe('trackId', () => {
    it('should return document id', () => {
      // WHEN
      const result = comp.trackId(0, { id: 999 } as any);

      // THEN
      expect(result).toBe(999);
    });
  });
});
