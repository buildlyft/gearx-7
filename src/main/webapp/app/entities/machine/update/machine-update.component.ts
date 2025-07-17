import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPartner } from 'app/entities/partner/partner.model';
import { PartnerService } from 'app/entities/partner/service/partner.service';
import { MachineStatus } from 'app/entities/enumerations/machine-status.model';
import { MachineService } from '../service/machine.service';
import { IMachine } from '../machine.model';
import { MachineFormService, MachineFormGroup } from './machine-form.service';

@Component({
  standalone: true,
  selector: 'jhi-machine-update',
  templateUrl: './machine-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MachineUpdateComponent implements OnInit {
  isSaving = false;
  machine: IMachine | null = null;
  machineStatusValues = Object.keys(MachineStatus);

  partnersSharedCollection: IPartner[] = [];

  editForm: MachineFormGroup = this.machineFormService.createMachineFormGroup();

  constructor(
    protected machineService: MachineService,
    protected machineFormService: MachineFormService,
    protected partnerService: PartnerService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  comparePartner = (o1: IPartner | null, o2: IPartner | null): boolean => this.partnerService.comparePartner(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ machine }) => {
      this.machine = machine;
      if (machine) {
        this.updateForm(machine);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const machine = this.machineFormService.getMachine(this.editForm);
    if (machine.id !== null) {
      this.subscribeToSaveResponse(this.machineService.update(machine));
    } else {
      this.subscribeToSaveResponse(this.machineService.create(machine));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMachine>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(machine: IMachine): void {
    this.machine = machine;
    this.machineFormService.resetForm(this.editForm, machine);

    this.partnersSharedCollection = this.partnerService.addPartnerToCollectionIfMissing<IPartner>(
      this.partnersSharedCollection,
      machine.partner,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.partnerService
      .query()
      .pipe(map((res: HttpResponse<IPartner[]>) => res.body ?? []))
      .pipe(map((partners: IPartner[]) => this.partnerService.addPartnerToCollectionIfMissing<IPartner>(partners, this.machine?.partner)))
      .subscribe((partners: IPartner[]) => (this.partnersSharedCollection = partners));
  }
}
