import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPartner } from '../partner.model';
import { PartnerService } from '../service/partner.service';
import { PartnerFormService, PartnerFormGroup } from './partner-form.service';

@Component({
  standalone: true,
  selector: 'jhi-partner-update',
  templateUrl: './partner-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PartnerUpdateComponent implements OnInit {
  isSaving = false;
  partner: IPartner | null = null;

  editForm: PartnerFormGroup = this.partnerFormService.createPartnerFormGroup();

  constructor(
    protected partnerService: PartnerService,
    protected partnerFormService: PartnerFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ partner }) => {
      this.partner = partner;
      if (partner) {
        this.updateForm(partner);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const partner = this.partnerFormService.getPartner(this.editForm);
    if (partner.id !== null) {
      this.subscribeToSaveResponse(this.partnerService.update(partner));
    } else {
      this.subscribeToSaveResponse(this.partnerService.create(partner));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPartner>>): void {
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

  protected updateForm(partner: IPartner): void {
    this.partner = partner;
    this.partnerFormService.resetForm(this.editForm, partner);
  }
}
