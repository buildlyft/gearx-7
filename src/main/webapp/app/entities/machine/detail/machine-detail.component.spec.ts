import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { MachineDetailComponent } from './machine-detail.component';

describe('Machine Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MachineDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MachineDetailComponent,
              resolve: { machine: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MachineDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load machine on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MachineDetailComponent);

      // THEN
      expect(instance.machine).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
