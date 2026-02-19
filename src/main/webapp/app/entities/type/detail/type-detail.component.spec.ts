import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TypeDetailComponent } from './type-detail.component';

describe('Type Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TypeDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TypeDetailComponent,
              resolve: { type: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    }).compileComponents();
  });

  it('Should load type on init', async () => {
    const harness = await RouterTestingHarness.create();
    const instance = await harness.navigateByUrl('/', TypeDetailComponent);
    expect(instance.type).toEqual(expect.objectContaining({ id: 123 }));
  });
});
