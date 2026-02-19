import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { MachineOperatorService } from '../service/machineOperator.service';
import machineOperatorResolve from './machineOperator-routing-resolve.service';

describe('machineOperatorResolve', () => {
  let service: MachineOperatorService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MachineOperatorService,
        {
          provide: Router,
          useValue: { navigate: jest.fn() },
        },
      ],
    });

    service = TestBed.inject(MachineOperatorService);
    router = TestBed.inject(Router);
  });

  it('should return operator if machineId exists', () => {
    const route = { params: { machineId: 123 } } as ActivatedRouteSnapshot;

    jest.spyOn(service, 'getByMachine').mockReturnValue(of(new HttpResponse({ body: { operatorId: 1, machineId: 123 } })));

    TestBed.runInInjectionContext(() => {
      machineOperatorResolve(route).subscribe(result => {
        expect(service.getByMachine).toHaveBeenCalledWith(123);
        expect(result).toEqual({ operatorId: 1, machineId: 123 });
      });
    });
  });

  it('should navigate to 404 if operator not found', () => {
    const route = { params: { machineId: 123 } } as ActivatedRouteSnapshot;

    jest.spyOn(service, 'getByMachine').mockReturnValue(of(new HttpResponse({ body: null })));

    TestBed.runInInjectionContext(() => {
      machineOperatorResolve(route).subscribe(result => {
        expect(router.navigate).toHaveBeenCalledWith(['404']);
        expect(result).toBeUndefined();
      });
    });
  });

  it('should return null if machineId not provided', () => {
    const route = { params: {} } as ActivatedRouteSnapshot;

    TestBed.runInInjectionContext(() => {
      machineOperatorResolve(route).subscribe(result => {
        expect(result).toBeNull();
      });
    });
  });
});
