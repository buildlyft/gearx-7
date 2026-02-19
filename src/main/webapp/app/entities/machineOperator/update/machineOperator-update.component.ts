import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { FormGroup } from '@angular/forms';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMachineOperator } from '../machineOperator.model';
import { MachineOperatorService } from '../service/machineOperator.service';
import { MachineOperatorFormService } from './machineOperator-form.service';

import { MachineService } from 'app/entities/machine/service/machine.service';
import { IMachine } from 'app/entities/machine/machine.model';

@Component({
  standalone: true,
  selector: 'jhi-machine-operator-update',
  templateUrl: './machineOperator-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MachineOperatorUpdateComponent implements OnInit {
  isSaving = false;
  machineOperator: IMachineOperator | null = null;
  selectedFile?: File;

  machines: IMachine[] = [];

  editForm: FormGroup = this.machineOperatorFormService.createMachineOperatorFormGroup();

  constructor(
    protected machineOperatorService: MachineOperatorService,
    protected machineOperatorFormService: MachineOperatorFormService,
    protected activatedRoute: ActivatedRoute,
    protected machineService: MachineService,
  ) {}

  get isEdit(): boolean {
    return !!this.machineOperator?.operatorId;
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ machineOperator }) => {
      this.machineOperator = machineOperator;
      if (machineOperator) {
        this.machineOperatorFormService.resetForm(this.editForm, machineOperator);
      }
    });

    this.loadMachines();
  }

  protected loadMachines(): void {
    this.machineService.queryWithoutOperator().subscribe({
      next: (res: HttpResponse<IMachine[]>) => {
        this.machines = res.body ?? [];
      },
    });
  }

  onFileChange(event: any): void {
    if (event.target.files?.length) {
      this.selectedFile = event.target.files[0];
    }
  }

  save(): void {
    this.isSaving = true;

    const operator = this.machineOperatorFormService.getMachineOperator(this.editForm);

    const formData = new FormData();
    formData.append('machineId', String(operator.machineId ?? ''));
    formData.append('driverName', operator.driverName ?? '');
    formData.append('operatorContact', operator.operatorContact ?? '');
    formData.append('address', operator.address ?? '');
    formData.append('active', String(operator.active ?? true));
    formData.append('licenseIssueDate', operator.licenseIssueDate ?? '');

    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }

    if (operator.operatorId !== null) {
      this.subscribeToSaveResponse(this.machineOperatorService.reassign(operator.machineId!, formData));
    } else {
      this.subscribeToSaveResponse(this.machineOperatorService.create(formData));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMachineOperator>>): void {
    result.pipe(finalize(() => (this.isSaving = false))).subscribe({
      next: () => this.previousState(),
    });
  }

  previousState(): void {
    window.history.back();
  }
}
