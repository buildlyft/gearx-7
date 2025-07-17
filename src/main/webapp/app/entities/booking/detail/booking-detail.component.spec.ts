import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { BookingDetailComponent } from './booking-detail.component';

describe('Booking Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookingDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: BookingDetailComponent,
              resolve: { booking: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BookingDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load booking on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BookingDetailComponent);

      // THEN
      expect(instance.booking).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
