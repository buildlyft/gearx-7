import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMachine } from '../machine.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../machine.test-samples';

import { MachineService, RestMachine } from './machine.service';

const requireRestSample: RestMachine = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
};

describe('Machine Service', () => {
  let service: MachineService;
  let httpMock: HttpTestingController;
  let expectedResult: IMachine | IMachine[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MachineService);
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

    it('should create a Machine', () => {
      const machine = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(machine).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Machine', () => {
      const machine = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(machine).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Machine', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Machine', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Machine', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMachineToCollectionIfMissing', () => {
      it('should add a Machine to an empty array', () => {
        const machine: IMachine = sampleWithRequiredData;
        expectedResult = service.addMachineToCollectionIfMissing([], machine);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(machine);
      });

      it('should not add a Machine to an array that contains it', () => {
        const machine: IMachine = sampleWithRequiredData;
        const machineCollection: IMachine[] = [
          {
            ...machine,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMachineToCollectionIfMissing(machineCollection, machine);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Machine to an array that doesn't contain it", () => {
        const machine: IMachine = sampleWithRequiredData;
        const machineCollection: IMachine[] = [sampleWithPartialData];
        expectedResult = service.addMachineToCollectionIfMissing(machineCollection, machine);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(machine);
      });

      it('should add only unique Machine to an array', () => {
        const machineArray: IMachine[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const machineCollection: IMachine[] = [sampleWithRequiredData];
        expectedResult = service.addMachineToCollectionIfMissing(machineCollection, ...machineArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const machine: IMachine = sampleWithRequiredData;
        const machine2: IMachine = sampleWithPartialData;
        expectedResult = service.addMachineToCollectionIfMissing([], machine, machine2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(machine);
        expect(expectedResult).toContain(machine2);
      });

      it('should accept null and undefined values', () => {
        const machine: IMachine = sampleWithRequiredData;
        expectedResult = service.addMachineToCollectionIfMissing([], null, machine, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(machine);
      });

      it('should return initial array if no Machine is added', () => {
        const machineCollection: IMachine[] = [sampleWithRequiredData];
        expectedResult = service.addMachineToCollectionIfMissing(machineCollection, undefined, null);
        expect(expectedResult).toEqual(machineCollection);
      });
    });

    describe('compareMachine', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMachine(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMachine(entity1, entity2);
        const compareResult2 = service.compareMachine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMachine(entity1, entity2);
        const compareResult2 = service.compareMachine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMachine(entity1, entity2);
        const compareResult2 = service.compareMachine(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
