import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
//import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { SubcategoryService } from '../service/subcategory.service';
import { ISubcategory } from '../subcategory.model';
import { SubcategoryFormService, SubcategoryFormGroup } from './subcategory-form.service';

@Component({
  standalone: true,
  selector: 'jhi-subcategory-update',
  templateUrl: './subcategory-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SubcategoryUpdateComponent implements OnInit {
  isSaving = false;
  subcategory: ISubcategory | null = null;
  selectedFile?: File;
  previewUrl: string | ArrayBuffer | null = null;

  //  Simple category list
  categories: ICategory[] = [];

  editForm: SubcategoryFormGroup = this.subcategoryFormService.createSubcategoryFormGroup();

  constructor(
    //  protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected subcategoryService: SubcategoryService,
    protected subcategoryFormService: SubcategoryFormService,
    protected categoryService: CategoryService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ subcategory }) => {
      this.subcategory = subcategory;
      if (subcategory) {
        this.updateForm(subcategory);
      }
      this.loadCategories(); // ✅ simple loading
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

    const subcategory = this.subcategoryFormService.getSubcategory(this.editForm);
    const formData = new FormData();

    // JSON part
    formData.append('subcategory', new Blob([JSON.stringify(subcategory)], { type: 'application/json' }));

    // File part
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }

    if (subcategory.id !== null) {
      this.subscribeToSaveResponse(this.subcategoryService.updateMultipart(subcategory.id, formData));
    } else {
      // Image required while creating
      if (!this.selectedFile) {
        alert('Image is required while creating Subcategory');
        this.isSaving = false;
        return;
      }

      this.subscribeToSaveResponse(this.subcategoryService.createMultipart(formData));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISubcategory>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
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

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // for override
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(subcategory: ISubcategory): void {
    this.subcategory = subcategory;
    this.subcategoryFormService.resetForm(this.editForm, subcategory);

    // Show existing image in preview (edit mode)
    if (subcategory.imageUrl) {
      this.previewUrl = subcategory.imageUrl;
    }
  }

  //  Clean category loader
  protected loadCategories(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .subscribe((categories: ICategory[]) => (this.categories = categories));
  }
}
