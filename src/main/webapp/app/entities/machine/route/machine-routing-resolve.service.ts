import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMachine } from '../machine.model';
import { MachineService } from '../service/machine.service';

export const machineResolve = (route: ActivatedRouteSnapshot): Observable<null | IMachine> => {
  const id = route.params['id'];
  if (id) {
    return inject(MachineService)
      .find(id)
      .pipe(
        mergeMap((machine: HttpResponse<IMachine>) => {
          if (machine.body) {
            return of(machine.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default machineResolve;
