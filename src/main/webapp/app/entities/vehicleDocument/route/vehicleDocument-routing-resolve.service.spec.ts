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

  it('should return IVehicleDocument when id is provided', () => {
    const id = 123;
    const route = new ActivatedRouteSnapshot();
    (route.params as any) = { id };

    const mockResponse: IVehicleDocument = { id: 123, fileName: 'test.pdf' };

    vehicleDocumentService.find.and.returnValue(of(new HttpResponse({ body: mockResponse })));

    let result: IVehicleDocument | null | undefined;
    service.resolve(route).subscribe(res => (result = res));

    expect(vehicleDocumentService.find).toHaveBeenCalledWith(id);
    expect(result).toEqual(mockResponse);
  });

  it('should return null when id is not provided', () => {
    const route = new ActivatedRouteSnapshot();

    let result: IVehicleDocument | null | undefined;
    service.resolve(route).subscribe(res => (result = res));

    expect(vehicleDocumentService.find).not.toHaveBeenCalled();
    expect(result).toBeNull();
  });

  it('should navigate to 404 when document not found', () => {
    const id = 123;
    const route = new ActivatedRouteSnapshot();
    (route.params as any) = { id };

    vehicleDocumentService.find.and.returnValue(of(new HttpResponse({ body: null })));

    let result: IVehicleDocument | null | undefined;
    service.resolve(route).subscribe(res => (result = res));

    expect(vehicleDocumentService.find).toHaveBeenCalledWith(id);
    expect(router.navigate).toHaveBeenCalledWith(['404']);
    expect(result).toBeUndefined();
  });
});
