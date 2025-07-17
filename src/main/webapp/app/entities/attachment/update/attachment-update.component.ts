import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPartner } from 'app/entities/partner/partner.model';
import { PartnerService } from 'app/entities/partner/service/partner.service';
import { IMachine } from 'app/entities/machine/machine.model';
import { MachineService } from 'app/entities/machine/service/machine.service';
import { AttachmentService } from '../service/attachment.service';
import { IAttachment } from '../attachment.model';
import { AttachmentFormService, AttachmentFormGroup } from './attachment-form.service';

@Component({
  standalone: true,
  selector: 'jhi-attachment-update',
  templateUrl: './attachment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AttachmentUpdateComponent implements OnInit {
  isSaving = false;
  attachment: IAttachment | null = null;

  partnersSharedCollection: IPartner[] = [];
  machinesSharedCollection: IMachine[] = [];

  editForm: AttachmentFormGroup = this.attachmentFormService.createAttachmentFormGroup();

  constructor(
    protected attachmentService: AttachmentService,
    protected attachmentFormService: AttachmentFormService,
    protected partnerService: PartnerService,
    protected machineService: MachineService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  comparePartner = (o1: IPartner | null, o2: IPartner | null): boolean => this.partnerService.comparePartner(o1, o2);

  compareMachine = (o1: IMachine | null, o2: IMachine | null): boolean => this.machineService.compareMachine(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ attachment }) => {
      this.attachment = attachment;
      if (attachment) {
        this.updateForm(attachment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const attachment = this.attachmentFormService.getAttachment(this.editForm);
    if (attachment.id !== null) {
      this.subscribeToSaveResponse(this.attachmentService.update(attachment));
    } else {
      this.subscribeToSaveResponse(this.attachmentService.create(attachment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAttachment>>): void {
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

  protected updateForm(attachment: IAttachment): void {
    this.attachment = attachment;
    this.attachmentFormService.resetForm(this.editForm, attachment);

    this.partnersSharedCollection = this.partnerService.addPartnerToCollectionIfMissing<IPartner>(
      this.partnersSharedCollection,
      attachment.partner,
    );
    this.machinesSharedCollection = this.machineService.addMachineToCollectionIfMissing<IMachine>(
      this.machinesSharedCollection,
      attachment.machine,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.partnerService
      .query()
      .pipe(map((res: HttpResponse<IPartner[]>) => res.body ?? []))
      .pipe(
        map((partners: IPartner[]) => this.partnerService.addPartnerToCollectionIfMissing<IPartner>(partners, this.attachment?.partner)),
      )
      .subscribe((partners: IPartner[]) => (this.partnersSharedCollection = partners));

    this.machineService
      .query()
      .pipe(map((res: HttpResponse<IMachine[]>) => res.body ?? []))
      .pipe(
        map((machines: IMachine[]) => this.machineService.addMachineToCollectionIfMissing<IMachine>(machines, this.attachment?.machine)),
      )
      .subscribe((machines: IMachine[]) => (this.machinesSharedCollection = machines));
  }
}
