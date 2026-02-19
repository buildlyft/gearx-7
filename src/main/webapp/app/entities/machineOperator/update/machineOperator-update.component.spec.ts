import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { MachineOperatorUpdateComponent } from './machineOperator-update.component';
import { MachineOperatorService } from '../service/machineOperator.service';
import { MachineOperatorFormService } from './machineOperator-form.service';

describe('MachineOperatorUpdateComponent', () => {
  let comp: MachineOperatorUpdateComponent;
  let fixture: ComponentFixture<MachineOperatorUpdateComponent>;
  let service: MachineOperatorService;
  let formService: MachineOperatorFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MachineOperatorUpdateComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ machineOperator: { operatorId: 123 } }) },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MachineOperatorUpdateComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MachineOperatorService);
    formService = TestBed.inject(MachineOperatorFormService);
  });

  it('Should update editForm on init', () => {
    comp.ngOnInit();
    expect(comp.machineOperator).toEqual(expect.objectContaining({ operatorId: 123 }));
  });

  it('Should call reassign on save for existing entity', () => {
    const saveSubject = new Subject<HttpResponse<any>>();
    jest.spyOn(formService, 'getMachineOperator').mockReturnValue({
      operatorId: 123,
      machineId: 1,
    } as any);
    jest.spyOn(service, 'reassign').mockReturnValue(saveSubject);

    comp.save();
    expect(comp.isSaving).toBe(true);

    saveSubject.next(new HttpResponse({ body: { operatorId: 123 } }));
    saveSubject.complete();

    expect(service.reassign).toHaveBeenCalled();
  });

  it('Should call create on save for new entity', () => {
    const saveSubject = new Subject<HttpResponse<any>>();
    jest.spyOn(formService, 'getMachineOperator').mockReturnValue({
      operatorId: null,
      machineId: 1,
    } as any);
    jest.spyOn(service, 'create').mockReturnValue(saveSubject);

    comp.save();
    expect(comp.isSaving).toBe(true);

    saveSubject.next(new HttpResponse({ body: { operatorId: 456 } }));
    saveSubject.complete();

    expect(service.create).toHaveBeenCalled();
  });
});
