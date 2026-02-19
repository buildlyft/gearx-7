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
  // GET ALL ACTIVE
  // ---------------------------------------------------
  it('should retrieve all active operators', () => {
    const mockResponse = [{ operatorId: 1, driverName: 'Ravi' }];

    service.getAllActive().subscribe(res => {
      expect(res.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/active`);
    expect(req.request.method).toBe('GET');

    req.flush(mockResponse);
  });

  // ---------------------------------------------------
  // GET BY MACHINE
  // ---------------------------------------------------
  it('should retrieve operator by machineId', () => {
    const machineId = 123;
    const mockResponse = { operatorId: 1, machineId };

    service.getByMachine(machineId).subscribe(res => {
      expect(res.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/machine/${machineId}`);
    expect(req.request.method).toBe('GET');

    req.flush(mockResponse);
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
  it('should reassign operator with multipart form data', () => {
    const machineId = 123;
    const formData = new FormData();
    formData.append('machineId', '123');

    service.reassign(machineId, formData).subscribe(res => {
      expect(res).toBeInstanceOf(HttpResponse);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/machine/${machineId}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body instanceof FormData).toBeTrue();

    req.flush({ operatorId: 1 });
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
});
