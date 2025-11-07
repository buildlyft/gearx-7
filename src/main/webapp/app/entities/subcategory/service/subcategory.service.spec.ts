import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISubcategory } from '../subcategory.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../subcategory.test-samples';

import { SubcategoryService } from './subcategory.service';

const requireRestSample: ISubcategory = {
  ...sampleWithRequiredData,
};

describe('Subcategory Service', () => {
  let service: SubcategoryService;
  let httpMock: HttpTestingController;
  let expectedResult: ISubcategory | ISubcategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SubcategoryService);
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

    it('should create a Subcategory', () => {
      const subcategory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(subcategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Subcategory', () => {
      const subcategory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(subcategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Subcategory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Subcategory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Subcategory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSubcategoryToCollectionIfMissing', () => {
      it('should add a Subcategory to an empty array', () => {
        const subcategory: ISubcategory = sampleWithRequiredData;
        expectedResult = service.addSubcategoryToCollectionIfMissing([], subcategory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(subcategory);
      });

      it('should not add a Subcategory to an array that contains it', () => {
        const subcategory: ISubcategory = sampleWithRequiredData;
        const subcategoryCollection: ISubcategory[] = [
          {
            ...subcategory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSubcategoryToCollectionIfMissing(subcategoryCollection, subcategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Subcategory to an array that doesn't contain it", () => {
        const subcategory: ISubcategory = sampleWithRequiredData;
        const subcategoryCollection: ISubcategory[] = [sampleWithPartialData];
        expectedResult = service.addSubcategoryToCollectionIfMissing(subcategoryCollection, subcategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(subcategory);
      });

      it('should add only unique Subcategory to an array', () => {
        const subcategoryArray: ISubcategory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const subcategoryCollection: ISubcategory[] = [sampleWithRequiredData];
        expectedResult = service.addSubcategoryToCollectionIfMissing(subcategoryCollection, ...subcategoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const subcategory: ISubcategory = sampleWithRequiredData;
        const subcategory2: ISubcategory = sampleWithPartialData;
        expectedResult = service.addSubcategoryToCollectionIfMissing([], subcategory, subcategory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(subcategory);
        expect(expectedResult).toContain(subcategory2);
      });

      it('should accept null and undefined values', () => {
        const subcategory: ISubcategory = sampleWithRequiredData;
        expectedResult = service.addSubcategoryToCollectionIfMissing([], null, subcategory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(subcategory);
      });

      it('should return initial array if no Subcategory is added', () => {
        const subcategoryCollection: ISubcategory[] = [sampleWithRequiredData];
        expectedResult = service.addSubcategoryToCollectionIfMissing(subcategoryCollection, undefined, null);
        expect(expectedResult).toEqual(subcategoryCollection);
      });
    });

    describe('compareSubcategory', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSubcategory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSubcategory(entity1, entity2);
        const compareResult2 = service.compareSubcategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSubcategory(entity1, entity2);
        const compareResult2 = service.compareSubcategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSubcategory(entity1, entity2);
        const compareResult2 = service.compareSubcategory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
