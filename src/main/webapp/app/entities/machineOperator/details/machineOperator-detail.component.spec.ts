import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MachineOperatorDetailComponent } from './machineOperator-detail.component';

describe('MachineOperatorDetailComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MachineOperatorDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MachineOperatorDetailComponent,
              resolve: { machineOperator: () => of({ operatorId: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    }).compileComponents();
  });

  it('Should load machineOperator on init', async () => {
    const harness = await RouterTestingHarness.create();
    const instance = await harness.navigateByUrl('/', MachineOperatorDetailComponent);

    expect(instance.machineOperator).toEqual(expect.objectContaining({ operatorId: 123 }));
  });
});
