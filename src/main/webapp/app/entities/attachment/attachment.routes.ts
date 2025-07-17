import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { AttachmentComponent } from './list/attachment.component';
import { AttachmentDetailComponent } from './detail/attachment-detail.component';
import { AttachmentUpdateComponent } from './update/attachment-update.component';
import AttachmentResolve from './route/attachment-routing-resolve.service';

const attachmentRoute: Routes = [
  {
    path: '',
    component: AttachmentComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AttachmentDetailComponent,
    resolve: {
      attachment: AttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AttachmentUpdateComponent,
    resolve: {
      attachment: AttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AttachmentUpdateComponent,
    resolve: {
      attachment: AttachmentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default attachmentRoute;
