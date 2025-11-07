import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
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

  categoriesSharedCollection: ICategory[] = [];

  editForm: SubcategoryFormGroup = this.subcategoryFormService.createSubcategoryFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected subcategoryService: SubcategoryService,
    protected subcategoryFormService: SubcategoryFormService,
    protected categoryService: CategoryService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ subcategory }) => {
      this.subcategory = subcategory;
      if (subcategory) {
        this.updateForm(subcategory);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
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
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const subcategory = this.subcategoryFormService.getSubcategory(this.editForm);
    if (subcategory.id !== null) {
      this.subscribeToSaveResponse(this.subcategoryService.update(subcategory));
    } else {
      this.subscribeToSaveResponse(this.subcategoryService.create(subcategory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISubcategory>>): void {
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

  protected updateForm(subcategory: ISubcategory): void {
    this.subcategory = subcategory;
    this.subcategoryFormService.resetForm(this.editForm, subcategory);

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing<ICategory>(
      this.categoriesSharedCollection,
      subcategory.category,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, this.subcategory?.category),
        ),
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));
  }
}
