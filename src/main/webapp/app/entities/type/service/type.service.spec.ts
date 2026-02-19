import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { TypeService } from './type.service';
import { sampleWithRequiredData } from '../type.test-samples';

describe('Type Service', () => {
  let service: TypeService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(TypeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should find a type', () => {
    service.find(123).subscribe();

    const req = httpMock.expectOne({ method: 'GET' });
    req.flush(sampleWithRequiredData);
  });

  afterEach(() => {
    httpMock.verify();
  });
});
