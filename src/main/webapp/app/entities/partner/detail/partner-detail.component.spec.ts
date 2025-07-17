import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PartnerDetailComponent } from './partner-detail.component';

describe('Partner Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PartnerDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PartnerDetailComponent,
              resolve: { partner: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PartnerDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load partner on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PartnerDetailComponent);

      // THEN
      expect(instance.partner).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
