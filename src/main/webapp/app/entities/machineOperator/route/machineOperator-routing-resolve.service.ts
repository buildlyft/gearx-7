import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMachineOperator } from '../machineOperator.model';
import { MachineOperatorService } from '../service/machineOperator.service';

export const machineOperatorResolve = (route: ActivatedRouteSnapshot) => {
  const machineId = route.params['machineId'];

  if (machineId) {
    return inject(MachineOperatorService)
      .getByMachine(machineId)
      .pipe(
        mergeMap((res: HttpResponse<IMachineOperator>) => {
          if (res.body) {
            return of(res.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }

  return of(null);
};

export default machineOperatorResolve;
