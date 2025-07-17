import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAttachment } from '../attachment.model';
import { AttachmentService } from '../service/attachment.service';

export const attachmentResolve = (route: ActivatedRouteSnapshot): Observable<null | IAttachment> => {
  const id = route.params['id'];
  if (id) {
    return inject(AttachmentService)
      .find(id)
      .pipe(
        mergeMap((attachment: HttpResponse<IAttachment>) => {
          if (attachment.body) {
            return of(attachment.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default attachmentResolve;
