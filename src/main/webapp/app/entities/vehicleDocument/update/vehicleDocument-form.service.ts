import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

import { VehicleDocumentRoutingResolveService } from './vehicleDocument-routing-resolve.service';
import { VehicleDocumentService } from '../service/vehicleDocument.service';
import { IVehicleDocument } from '../vehicleDocument.model';

describe('VehicleDocumentRoutingResolveService', () => {
  let service: VehicleDocumentRoutingResolveService;
  let vehicleDocumentService: jasmine.SpyObj<VehicleDocumentService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const vehicleDocumentServiceSpy = jasmine.createSpyObj('VehicleDocumentService', ['find']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        VehicleDocumentRoutingResolveService,
        { provide: VehicleDocumentService, useValue: vehicleDocumentServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    });

    service = TestBed.inject(VehicleDocumentRoutingResolveService);
    vehicleDocumentService = TestBed.inject(VehicleDocumentService) as jasmine.SpyObj<VehicleDocumentService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  describe('resolve', () => {
    it('should return document when id is provided', () => {
      // GIVEN
      const id = 123;
      const route = new ActivatedRouteSnapshot();
      (route.params as any) = { id };

      const mockDocument: IVehicleDocument = {
        id: 123,
        fileName: 'test.pdf',
      };

      vehicleDocumentService.find.and.returnValue(of(new HttpResponse({ body: mockDocument })));

      // WHEN
      let result: IVehicleDocument | null | undefined;
      service.resolve(route).subscribe(res => (result = res));

      // THEN
      expect(vehicleDocumentService.find).toHaveBeenCalledWith(123);
      expect(result).toEqual(mockDocument);
    });

    it('should return null when id is not provided', () => {
      // GIVEN
      const route = new ActivatedRouteSnapshot();

      // WHEN
      let result: IVehicleDocument | null | undefined;
      service.resolve(route).subscribe(res => (result = res));

      // THEN
      expect(vehicleDocumentService.find).not.toHaveBeenCalled();
      expect(result).toBeNull();
    });

    it('should navigate to 404 if document not found', () => {
      // GIVEN
      const id = 123;
      const route = new ActivatedRouteSnapshot();
      (route.params as any) = { id };

      vehicleDocumentService.find.and.returnValue(of(new HttpResponse({ body: null })));

      // WHEN
      let result: IVehicleDocument | null | undefined;
      service.resolve(route).subscribe(res => (result = res));

      // THEN
      expect(vehicleDocumentService.find).toHaveBeenCalledWith(123);
      expect(router.navigate).toHaveBeenCalledWith(['404']);
      expect(result).toBeUndefined();
    });
  });
});
