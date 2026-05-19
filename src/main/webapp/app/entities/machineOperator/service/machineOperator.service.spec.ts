import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpResponse } from '@angular/common/http';

import { MachineOperatorService } from './machineOperator.service';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

describe('MachineOperatorService', () => {
  let service: MachineOperatorService;
  let httpMock: HttpTestingController;

  const mockApiUrl = 'api/machine-operators';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        MachineOperatorService,
        {
          provide: ApplicationConfigService,
          useValue: {
            getEndpointFor: () => mockApiUrl,
          },
        },
      ],
    });

    service = TestBed.inject(MachineOperatorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // ---------------------------------------------------
  // GET ALL operators by partner
  // ---------------------------------------------------
  it('should retrieve all operators by partner', () => {
    const mockResponse = [{ operatorId: 1, driverName: 'Ravi' }];

    service.getAllByPartner().subscribe(res => {
      expect(res.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/partner`);

    expect(req.request.method).toBe('GET');

    req.flush({
      success: true,
      status: 200,
      message: 'success',
      data: mockResponse,
    });
  });

  // ---------------------------------------------------
  // GET BY MACHINE
  // ---------------------------------------------------
  it('should retrieve operator by id', () => {
    const operatorId = 1;

    const mockResponse = {
      operatorId: 1,
      driverName: 'Ravi',
    };

    service.find(operatorId).subscribe(res => {
      expect(res.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/${operatorId}`);

    expect(req.request.method).toBe('GET');

    req.flush({
      success: true,
      status: 200,
      message: 'success',
      data: mockResponse,
    });
  });

  // ---------------------------------------------------
  // CREATE
  // ---------------------------------------------------
  it('should create operator with multipart form data', () => {
    const formData = new FormData();
    formData.append('machineId', '123');

    service.create(formData).subscribe(res => {
      expect(res).toBeInstanceOf(HttpResponse);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/create_and_assign`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body instanceof FormData).toBeTrue();

    req.flush({ operatorId: 1 });
  });

  // ---------------------------------------------------
  // REASSIGN
  // ---------------------------------------------------
  it('should update operator with multipart form data', () => {
    const operatorId = 1;

    const formData = new FormData();

    formData.append('driverName', 'Ravi');

    service.update(operatorId, formData).subscribe(res => {
      expect(res).toBeInstanceOf(HttpResponse);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/${operatorId}`);

    expect(req.request.method).toBe('PUT');

    expect(req.request.body instanceof FormData).toBeTrue();

    req.flush({
      success: true,
      status: 200,
      message: 'updated',
      data: { operatorId: 1 },
    });
  });

  // ---------------------------------------------------
  // DELETE
  // ---------------------------------------------------
  it('should delete operator', () => {
    const operatorId = 1;

    service.delete(operatorId).subscribe(res => {
      expect(res).toBeInstanceOf(HttpResponse);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/${operatorId}`);
    expect(req.request.method).toBe('DELETE');

    req.flush({});
  });

  it('should partially update operator', () => {
    const operatorId = 1;

    const formData = new FormData();

    formData.append('driverName', 'Ravi');

    service.partialUpdate(operatorId, formData).subscribe(res => {
      expect(res).toBeInstanceOf(HttpResponse);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/${operatorId}`);

    expect(req.request.method).toBe('PATCH');

    expect(req.request.body instanceof FormData).toBeTrue();

    req.flush({
      success: true,
      status: 200,
      message: 'patched',
      data: { operatorId: 1 },
    });
  });
});
