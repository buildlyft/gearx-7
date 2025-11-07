import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SubcategoryComponent } from './list/subcategory.component';
import { SubcategoryDetailComponent } from './detail/subcategory-detail.component';
import { SubcategoryUpdateComponent } from './update/subcategory-update.component';
import SubcategoryResolve from './route/subcategory-routing-resolve.service';

const subcategoryRoute: Routes = [
  {
    path: '',
    component: SubcategoryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SubcategoryDetailComponent,
    resolve: {
      subcategory: SubcategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SubcategoryUpdateComponent,
    resolve: {
      subcategory: SubcategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SubcategoryUpdateComponent,
    resolve: {
      subcategory: SubcategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default subcategoryRoute;
