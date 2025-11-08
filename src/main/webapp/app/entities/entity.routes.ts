import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'customer',
    data: { pageTitle: 'Customers' },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'machine',
    data: { pageTitle: 'Machines' },
    loadChildren: () => import('./machine/machine.routes'),
  },
  {
    path: 'booking',
    data: { pageTitle: 'Bookings' },
    loadChildren: () => import('./booking/booking.routes'),
  },
  {
    path: 'category',
    data: { pageTitle: 'Categories' },
    loadChildren: () => import('./category/category.routes'),
  },
  {
    path: 'subcategory',
    data: { pageTitle: 'Subcategories' },
    loadChildren: () => import('./subcategory/subcategory.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
