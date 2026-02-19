import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { TypeService } from '../service/type.service';
import typeResolve from './type-routing-resolve.service';

describe('Type routing resolve service', () => {
  let mockRouter: Router;
  let service: TypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: Router,
          useValue: { navigate: jest.fn() },
        },
      ],
    });

    mockRouter = TestBed.inject(Router);
    service = TestBed.inject(TypeService);
  });

  it('should return Type returned by find', () => {
    const route = { params: { id: 123 } } as ActivatedRouteSnapshot;
    jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: { id: 123 } })));

    TestBed.runInInjectionContext(() => {
      typeResolve(route).subscribe(result => {
        expect(service.find).toHaveBeenCalledWith(123);
        expect(result).toEqual({ id: 123 });
      });
    });
  });
});
