import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { VehicleDocumentService } from './vehicleDocument.service';
import { IVehicleDocument, IVehicleDocumentResponse } from '../vehicleDocument.model';

describe('VehicleDocumentService', () => {
  let service: VehicleDocumentService;
  let httpMock: HttpTestingController;
  const resourceUrl = '/api/vehicle-documents';

  const sampleResponse: IVehicleDocumentResponse = {
    machineId: 1,
    documents: [
      {
        id: 123,
        docType: 'RC',
        fileName: 'rc.pdf',
        fileUrl: 'http://test.com/rc.pdf',
        contentType: 'application/pdf',
        size: 1024,
        uploadedAt: '2026-01-01T10:00:00Z',
        uploadedBy: 'admin',
      } as IVehicleDocument,
    ],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [VehicleDocumentService],
    });

    service = TestBed.inject(VehicleDocumentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getMachineDocuments', () => {
    it('should call GET /machine/{machineId} and return documents', () => {
      // WHEN
      service.getMachineDocuments(1).subscribe(res => {
        // THEN
        expect(res).toEqual(sampleResponse);
        expect(res.machineId).toBe(1);
        expect(res.documents?.length).toBe(1);
      });

      const req = httpMock.expectOne(`${resourceUrl}/machine/1`);
      expect(req.request.method).toBe('GET');
      req.flush(sampleResponse);
    });
  });

  describe('getAllDocuments', () => {
    it('should call GET /all and return list', () => {
      const mockList: IVehicleDocumentResponse[] = [sampleResponse];

      // WHEN
      service.getAllDocuments().subscribe(res => {
        // THEN
        expect(res).toEqual(mockList);
        expect(res.length).toBe(1);
      });

      const req = httpMock.expectOne(`${resourceUrl}/all`);
      expect(req.request.method).toBe('GET');
      req.flush(mockList);
    });
  });

  describe('uploadDocuments', () => {
    it('should call POST /bulk-upload with FormData', () => {
      // GIVEN
      const files: File[] = [new File(['test'], 'test.pdf', { type: 'application/pdf' })];

      // WHEN
      service.uploadDocuments(5, null, files).subscribe(res => {
        // THEN
        expect(res).toEqual(sampleResponse);
      });

      const req = httpMock.expectOne(`${resourceUrl}/bulk-upload`);
      expect(req.request.method).toBe('POST');

      // Validate FormData
      const body = req.request.body as FormData;
      expect(body.has('machineId')).toBeTrue();
      expect(body.has('files')).toBeTrue();

      req.flush(sampleResponse);
    });

    it('should include uploadedBy when provided', () => {
      const files: File[] = [new File(['data'], 'doc.png', { type: 'image/png' })];

      // WHEN
      service.uploadDocuments(10, 99, files).subscribe();

      const req = httpMock.expectOne(`${resourceUrl}/bulk-upload`);
      const body = req.request.body as FormData;

      expect(body.get('machineId')).toBe('10');
      expect(body.get('uploadedBy')).toBe('99');

      req.flush(sampleResponse);
    });
  });

  describe('deleteDocument', () => {
    it('should call DELETE /{id}', () => {
      // WHEN
      service.deleteDocument(123).subscribe(res => {
        // THEN
        expect(res).toBeNull();
      });

      const req = httpMock.expectOne(`${resourceUrl}/123`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
