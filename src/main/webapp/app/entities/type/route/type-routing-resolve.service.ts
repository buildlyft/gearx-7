import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IType } from '../type.model';
import { TypeService } from '../service/type.service';

export const typeResolve = (route: ActivatedRouteSnapshot) => {
  const id = route.params['id'];
  if (id) {
    return inject(TypeService)
      .find(id)
      .pipe(
        mergeMap((res: HttpResponse<IType>) => {
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

export default typeResolve;
