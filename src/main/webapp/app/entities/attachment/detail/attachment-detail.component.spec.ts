import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AttachmentDetailComponent } from './attachment-detail.component';

describe('Attachment Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AttachmentDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: AttachmentDetailComponent,
              resolve: { attachment: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AttachmentDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load attachment on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AttachmentDetailComponent);

      // THEN
      expect(instance.attachment).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
