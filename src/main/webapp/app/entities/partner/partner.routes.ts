import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PartnerComponent } from './list/partner.component';
import { PartnerDetailComponent } from './detail/partner-detail.component';
import { PartnerUpdateComponent } from './update/partner-update.component';
import PartnerResolve from './route/partner-routing-resolve.service';

const partnerRoute: Routes = [
  {
    path: '',
    component: PartnerComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PartnerDetailComponent,
    resolve: {
      partner: PartnerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PartnerUpdateComponent,
    resolve: {
      partner: PartnerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PartnerUpdateComponent,
    resolve: {
      partner: PartnerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default partnerRoute;
