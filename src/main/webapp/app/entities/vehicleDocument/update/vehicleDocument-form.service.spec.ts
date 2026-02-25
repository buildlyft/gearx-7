import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { VehicleDocumentUpdateComponent } from './vehicleDocument-update.component';
import { VehicleDocumentService } from '../service/vehicleDocument.service';

describe('VehicleDocumentUpdateComponent', () => {
  let comp: VehicleDocumentUpdateComponent;
  let fixture: ComponentFixture<VehicleDocumentUpdateComponent>;
  let service: jasmine.SpyObj<VehicleDocumentService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const serviceSpy = jasmine.createSpyObj('VehicleDocumentService', ['uploadDocuments']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [VehicleDocumentUpdateComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({ machineId: 1 }),
            queryParams: of({}),
          },
        },
        { provide: VehicleDocumentService, useValue: serviceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    })
      .overrideTemplate(VehicleDocumentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VehicleDocumentUpdateComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(VehicleDocumentService) as jasmine.SpyObj<VehicleDocumentService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should initialize machineId from route params', () => {
    comp.ngOnInit();
    expect(comp.machineId).toBe(1);
  });

  it('should call uploadDocuments on save', () => {
    comp.machineId = 1;
    comp.selectedFiles = [new File(['test'], 'test.pdf')];

    service.uploadDocuments.and.returnValue(of({ machineId: 1, documents: [] }));

    comp.save();

    expect(service.uploadDocuments).toHaveBeenCalledWith(1, null, comp.selectedFiles);
  });

  it('should navigate back on successful save', () => {
    comp.machineId = 1;
    comp.selectedFiles = [new File(['test'], 'test.pdf')];

    service.uploadDocuments.and.returnValue(of({ machineId: 1, documents: [] }));

    comp.save();

    expect(router.navigate).toHaveBeenCalledWith(['/vehicle-document'], {
      queryParams: { machineId: 1 },
    });
  });
});
