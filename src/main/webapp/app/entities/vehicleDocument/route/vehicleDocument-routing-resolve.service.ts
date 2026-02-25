import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

import { IVehicleDocument } from '../vehicleDocument.model';
import { VehicleDocumentService } from '../service/vehicleDocument.service';

@Injectable({ providedIn: 'root' })
export default class VehicleDocumentRoutingResolveService {
  constructor(
    protected service: VehicleDocumentService,
    protected router: Router,
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVehicleDocument | null> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        map((res: HttpResponse<IVehicleDocument>) => {
          if (res.body) {
            return res.body;
          } else {
            this.router.navigate(['404']);
            return null;
          }
        }),
      );
    }
    return of(null);
  }
}
