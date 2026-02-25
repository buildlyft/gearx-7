import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TypeService } from '../service/type.service';
import { IType } from '../type.model';

import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { AlertError } from 'app/shared/alert/alert-error.model';

@Component({
  standalone: true,
  selector: 'jhi-type-update',
  templateUrl: './type-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TypeUpdateComponent implements OnInit {
  isSaving = false;
  type: IType | null = null;
  selectedFile?: File;
  previewUrl: string | ArrayBuffer | null = null;

  editForm: FormGroup = this.fb.group({
    id: [],
    typeName: [null, [Validators.required]],
  });

  constructor(
    protected typeService: TypeService,
    protected activatedRoute: ActivatedRoute,
    protected eventManager: EventManager,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ type }) => {
      this.type = type;
      if (type) {
        this.editForm.patchValue(type);
      }
    });
  }

  onFileChange(event: any): void {
    const file = event.target.files?.[0];

    if (file) {
      this.selectedFile = file;

      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }

  save(): void {
    this.isSaving = true;
    const type: IType = this.editForm.value;

    if (type.id !== null && type.id !== undefined) {
      this.subscribeToSaveResponse(this.typeService.update(type, this.selectedFile));
    } else {
      if (!this.selectedFile) {
        alert('Image is required while creating Type');
        this.isSaving = false;
        return;
      }
      this.subscribeToSaveResponse(this.typeService.create(type as any, this.selectedFile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IType>>): void {
    result.pipe(finalize(() => (this.isSaving = false))).subscribe({
      next: () => this.previousState(),
      error: error => this.handleError(error),
    });
  }

  protected handleError(error: any): void {
    if (error.status === 403) {
      this.eventManager.broadcast(
        new EventWithContent<AlertError>('gearx7App.error', {
          message: 'You are not authorized to perform this action.',
        }),
      );
    } else {
      this.eventManager.broadcast(
        new EventWithContent<AlertError>('gearx7App.error', {
          message: 'An unexpected error occurred.',
        }),
      );
    }
  }

  previousState(): void {
    window.history.back();
  }
}
