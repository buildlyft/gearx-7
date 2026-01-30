import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { MachineStatus } from 'app/entities/enumerations/machine-status.model';
import { MachineService } from '../service/machine.service';
import { IMachine } from '../machine.model';
import { MachineFormService, MachineFormGroup } from './machine-form.service';
import { CategoryService } from 'app/entities/category/service/category.service';
import { SubcategoryService } from 'app/entities/subcategory/service/subcategory.service';
import { ICategory } from 'app/entities/category/category.model';
import { ISubcategory } from 'app/entities/subcategory/subcategory.model';

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
  categories: ICategory[] = [];
  subcategories: ISubcategory[] = [];

  editForm: MachineFormGroup = this.machineFormService.createMachineFormGroup();

  constructor(
    protected machineService: MachineService,
    protected machineFormService: MachineFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected categoryService: CategoryService,
    protected subcategoryService: SubcategoryService,
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  // ---------------------------------------------------
  // INIT
  // ---------------------------------------------------
  ngOnInit(): void {
    this.loadRelationshipsOptions();
    this.activatedRoute.data.subscribe(({ machine }) => {
      this.machine = machine;

      if (machine) {
        this.updateForm(machine);

        // load subcategories when editing
        if (machine.categoryId) {
          this.loadSubcategories(machine.categoryId);
        }
      }
    });
  }

  // ---------------------------------------------------
  // LOAD DROPDOWNS
  // ---------------------------------------------------
  protected loadRelationshipsOptions(): void {
    // USERS (ADMIN)
    this.userService
      .query()
      .pipe(map(res => res.body ?? []))
      .pipe(map(users => this.userService.addUserToCollectionIfMissing(users, this.machine?.user)))
      .subscribe(users => (this.usersSharedCollection = users));

    // CATEGORIES
    this.categoryService.query().subscribe(res => {
      this.categories = res.body ?? [];
    });
  }

  onCategoryChange(): void {
    const categoryId = Number(this.editForm.get('categoryId')!.value);
    this.subcategories = [];
    this.editForm.get('subcategoryId')!.setValue(null);
    this.editForm.get('subcategoryId')!.markAsPristine();
    this.editForm.get('subcategoryId')!.markAsUntouched();

    if (categoryId) {
      this.loadSubcategories(categoryId);
    }
  }

  private loadSubcategories(categoryId: number): void {
    this.subcategoryService.query({ 'categoryId.equals': categoryId }).subscribe(res => {
      this.subcategories = res.body ?? [];
    });
  }

  // ---------------------------------------------------
  // SAVE
  // ---------------------------------------------------
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
    // handled by jhi-alert-error
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  previousState(): void {
    window.history.back();
  }

  // ---------------------------------------------------
  // FORM
  // ---------------------------------------------------
  protected updateForm(machine: IMachine): void {
    this.machine = machine;
    this.machineFormService.resetForm(this.editForm, machine);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, machine.user);
  }
}
