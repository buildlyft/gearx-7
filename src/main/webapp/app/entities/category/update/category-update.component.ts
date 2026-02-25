import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
//import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { CategoryService } from '../service/category.service';
import { ICategory } from '../category.model';
import { CategoryFormService, CategoryFormGroup } from './category-form.service';
import { TypeService } from 'app/entities/type/service/type.service';
import { IType } from 'app/entities/type/type.model';

@Component({
  standalone: true,
  selector: 'jhi-category-update',
  templateUrl: './category-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CategoryUpdateComponent implements OnInit {
  types: IType[] = [];
  isSaving = false;
  category: ICategory | null = null;
  selectedFile?: File;
  previewUrl: string | ArrayBuffer | null = null;

  editForm: CategoryFormGroup = this.categoryFormService.createCategoryFormGroup();

  constructor(
    //  protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected categoryService: CategoryService,
    protected categoryFormService: CategoryFormService,
    protected activatedRoute: ActivatedRoute,
    protected typeService: TypeService,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ category }) => {
      this.category = category;
      if (category) {
        this.updateForm(category);
      }
    });
    this.typeService.query().subscribe(res => {
      this.types = res.body ?? [];
    });
  }

  /* byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('gearx7App.error', { message: err.message })),
    });
  } */

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;

    const category = this.categoryFormService.getCategory(this.editForm);

    const formData = new FormData();
    formData.append('category', new Blob([JSON.stringify(category)], { type: 'application/json' }));

    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }

    if (category.id !== null) {
      // Partial update (PATCH)
      this.subscribeToSaveResponse(this.categoryService.patchMultipart(category.id, formData));
    } else {
      // Create requires image
      if (!this.selectedFile) {
        alert('Image is required while creating Category');
        this.isSaving = false;
        return;
      }
      this.subscribeToSaveResponse(this.categoryService.createMultipart(formData));
    }
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.selectedFile = file;

      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICategory>>): void {
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

  protected updateForm(category: ICategory): void {
    this.category = category;
    this.categoryFormService.resetForm(this.editForm, category);

    // Auto show existing image in preview (edit mode)
    if (category.imageUrl) {
      this.previewUrl = category.imageUrl;
    }
  }
}
