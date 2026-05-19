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

@Component({
  standalone: true,
  selector: 'jhi-machine-operator-update',
  templateUrl: './machineOperator-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MachineOperatorUpdateComponent implements OnInit {
  isSaving = false;
  machineOperator: IMachineOperator | null = null;
  selectedPhotoFile?: File;
  photoPreviewUrl: string | ArrayBuffer | null = null;

  selectedLicenseFile?: File;
  licensePreviewUrl: string | ArrayBuffer | null = null;

  editForm: FormGroup = this.machineOperatorFormService.createMachineOperatorFormGroup();

  constructor(
    protected machineOperatorService: MachineOperatorService,
    protected machineOperatorFormService: MachineOperatorFormService,
    protected activatedRoute: ActivatedRoute,
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
  }

  onLicenseChange(event: any): void {
    if (event.target.files?.length) {
      this.selectedLicenseFile = event.target.files[0];

      const reader = new FileReader();

      reader.onload = () => {
        this.licensePreviewUrl = reader.result;
      };

      reader.readAsDataURL(this.selectedLicenseFile as Blob);
    }
  }

  onPhotoChange(event: any): void {
    if (event.target.files?.length) {
      this.selectedPhotoFile = event.target.files[0];

      const reader = new FileReader();

      reader.onload = () => {
        this.photoPreviewUrl = reader.result;
      };

      reader.readAsDataURL(this.selectedPhotoFile as Blob);
    }
  }

  save(): void {
    this.isSaving = true;

    const operator = this.machineOperatorFormService.getMachineOperator(this.editForm);

    const formData = new FormData();

    formData.append('driverName', operator.driverName ?? '');

    formData.append('operatorContact', operator.operatorContact ?? '');

    formData.append('address', operator.address ?? '');

    formData.append('licenseIssueDate', operator.licenseIssueDate ?? '');

    // operator photo
    if (this.selectedPhotoFile) {
      formData.append('photo', this.selectedPhotoFile);
    }

    // operator license
    if (this.selectedLicenseFile) {
      formData.append('license', this.selectedLicenseFile);
    }

    // UPDATE
    if (operator.operatorId !== null) {
      this.subscribeToSaveResponse(this.machineOperatorService.update(operator.operatorId, formData));
    }

    // CREATE
    else {
      this.subscribeToSaveResponse(this.machineOperatorService.create(formData));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMachineOperator>>): void {
    result.pipe(finalize(() => (this.isSaving = false))).subscribe({
      next: () => this.previousState(),

      error: err => {
        const operator = this.machineOperatorFormService.getMachineOperator(this.editForm);

        alert(err?.error?.message ?? this.getAccessDeniedMessage(operator));
      },
    });
  }

  private getAccessDeniedMessage(operator: IMachineOperator | any): string {
    // create
    if (operator.operatorId === null) {
      return "You don't have any access to create a machine operator";
    }

    // update
    return "You don't have any access to update a machine operator";
  }

  previousState(): void {
    window.history.back();
  }
}
