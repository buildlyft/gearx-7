import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { ISubcategory } from 'app/entities/subcategory/subcategory.model';
import { SubcategoryService } from 'app/entities/subcategory/service/subcategory.service';
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

  usersSharedCollection: IUser[] = [];
  categoriesSharedCollection: ICategory[] = [];
  subcategoriesSharedCollection: ISubcategory[] = [];

  editForm: MachineFormGroup = this.machineFormService.createMachineFormGroup();

  constructor(
    protected machineService: MachineService,
    protected machineFormService: MachineFormService,
    protected userService: UserService,
    protected categoryService: CategoryService,
    protected subcategoryService: SubcategoryService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);
  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);
  compareSubcategory = (o1: ISubcategory | null, o2: ISubcategory | null): boolean => this.subcategoryService.compareSubcategory(o1, o2);

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

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, machine.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.machine?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));

    // Load subcategories based on selected category
    if (this.machine?.categoryId) {
      this.loadSubcategoriesForCategory(this.machine.categoryId);
    } else {
      this.subcategoryService
        .query()
        .pipe(map((res: HttpResponse<ISubcategory[]>) => res.body ?? []))
        .subscribe((subcategories: ISubcategory[]) => (this.subcategoriesSharedCollection = subcategories));
    }
  }

  protected loadSubcategoriesForCategory(categoryId: number): void {
    this.subcategoryService
      .query({ 'categoryId.equals': categoryId })
      .pipe(map((res: HttpResponse<ISubcategory[]>) => res.body ?? []))
      .subscribe((subcategories: ISubcategory[]) => (this.subcategoriesSharedCollection = subcategories));
  }

  onCategoryChange(categoryId: number | null): void {
    if (categoryId) {
      this.loadSubcategoriesForCategory(categoryId);
      // Clear subcategory selection when category changes
      this.editForm.patchValue({ subcategoryId: null });
    } else {
      this.subcategoriesSharedCollection = [];
      this.editForm.patchValue({ subcategoryId: null });
    }
  }
}
