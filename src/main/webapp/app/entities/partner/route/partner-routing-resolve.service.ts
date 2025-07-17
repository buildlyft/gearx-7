import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPartner } from '../partner.model';
import { PartnerService } from '../service/partner.service';

export const partnerResolve = (route: ActivatedRouteSnapshot): Observable<null | IPartner> => {
  const id = route.params['id'];
  if (id) {
    return inject(PartnerService)
      .find(id)
      .pipe(
        mergeMap((partner: HttpResponse<IPartner>) => {
          if (partner.body) {
            return of(partner.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default partnerResolve;
