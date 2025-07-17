import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPartner } from '../partner.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../partner.test-samples';

import { PartnerService } from './partner.service';

const requireRestSample: IPartner = {
  ...sampleWithRequiredData,
};

describe('Partner Service', () => {
  let service: PartnerService;
  let httpMock: HttpTestingController;
  let expectedResult: IPartner | IPartner[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PartnerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Partner', () => {
      const partner = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(partner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Partner', () => {
      const partner = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(partner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Partner', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Partner', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Partner', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPartnerToCollectionIfMissing', () => {
      it('should add a Partner to an empty array', () => {
        const partner: IPartner = sampleWithRequiredData;
        expectedResult = service.addPartnerToCollectionIfMissing([], partner);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(partner);
      });

      it('should not add a Partner to an array that contains it', () => {
        const partner: IPartner = sampleWithRequiredData;
        const partnerCollection: IPartner[] = [
          {
            ...partner,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPartnerToCollectionIfMissing(partnerCollection, partner);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Partner to an array that doesn't contain it", () => {
        const partner: IPartner = sampleWithRequiredData;
        const partnerCollection: IPartner[] = [sampleWithPartialData];
        expectedResult = service.addPartnerToCollectionIfMissing(partnerCollection, partner);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(partner);
      });

      it('should add only unique Partner to an array', () => {
        const partnerArray: IPartner[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const partnerCollection: IPartner[] = [sampleWithRequiredData];
        expectedResult = service.addPartnerToCollectionIfMissing(partnerCollection, ...partnerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const partner: IPartner = sampleWithRequiredData;
        const partner2: IPartner = sampleWithPartialData;
        expectedResult = service.addPartnerToCollectionIfMissing([], partner, partner2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(partner);
        expect(expectedResult).toContain(partner2);
      });

      it('should accept null and undefined values', () => {
        const partner: IPartner = sampleWithRequiredData;
        expectedResult = service.addPartnerToCollectionIfMissing([], null, partner, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(partner);
      });

      it('should return initial array if no Partner is added', () => {
        const partnerCollection: IPartner[] = [sampleWithRequiredData];
        expectedResult = service.addPartnerToCollectionIfMissing(partnerCollection, undefined, null);
        expect(expectedResult).toEqual(partnerCollection);
      });
    });

    describe('comparePartner', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePartner(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePartner(entity1, entity2);
        const compareResult2 = service.comparePartner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePartner(entity1, entity2);
        const compareResult2 = service.comparePartner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePartner(entity1, entity2);
        const compareResult2 = service.comparePartner(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
