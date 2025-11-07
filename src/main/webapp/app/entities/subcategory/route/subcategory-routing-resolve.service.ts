import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISubcategory } from '../subcategory.model';
import { SubcategoryService } from '../service/subcategory.service';

export const subcategoryResolve = (route: ActivatedRouteSnapshot): Observable<null | ISubcategory> => {
  const id = route.params['id'];
  if (id) {
    return inject(SubcategoryService)
      .find(id)
      .pipe(
        mergeMap((subcategory: HttpResponse<ISubcategory>) => {
          if (subcategory.body) {
            return of(subcategory.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default subcategoryResolve;
