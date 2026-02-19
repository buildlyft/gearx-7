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
import { TypeService } from 'app/entities/type/service/type.service';
import { IType } from 'app/entities/type/type.model';

@Component({
  standalone: true,
  selector: 'jhi-machine-update',
  templateUrl: './machine-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MachineUpdateComponent implements OnInit {
  types: IType[] = [];
  categories: ICategory[] = [];
  subcategories: ISubcategory[] = [];

  isSaving = false;
  machine: IMachine | null = null;

  machineStatusValues = Object.keys(MachineStatus);
  usersSharedCollection: IUser[] = [];

  editForm: MachineFormGroup = this.machineFormService.createMachineFormGroup();

  constructor(
    protected machineService: MachineService,
    protected machineFormService: MachineFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected categoryService: CategoryService,
    protected subcategoryService: SubcategoryService,
    protected typeService: TypeService,
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  // =====================================================
  // INIT
  // =====================================================
  ngOnInit(): void {
    this.loadUsers();
    this.loadTypes();

    this.activatedRoute.data.subscribe(({ machine }) => {
      this.machine = machine;

      if (machine) {
        this.updateForm(machine);

        if (machine.typeId) {
          this.loadCategories(machine.typeId);

          if (machine.categoryId) {
            this.loadSubcategories(machine.categoryId);
          }
        }
      }
    });
  }

  // =====================================================
  // LOAD INITIAL DATA
  // =====================================================
  private loadUsers(): void {
    this.userService
      .query()
      .pipe(map(res => res.body ?? []))
      .pipe(map(users => this.userService.addUserToCollectionIfMissing(users, this.machine?.user)))
      .subscribe(users => (this.usersSharedCollection = users));
  }

  private loadTypes(): void {
    this.typeService.query().subscribe(res => {
      this.types = res.body ?? [];
    });
  }

  // =====================================================
  // TYPE CHANGE → LOAD CATEGORIES
  // =====================================================
  onTypeChange(): void {
    const typeId = Number(this.editForm.get('typeId')!.value);

    // Reset lower dropdowns
    this.categories = [];
    this.subcategories = [];

    this.editForm.get('categoryId')!.setValue(null);
    this.editForm.get('subcategoryId')!.setValue(null);

    if (typeId) {
      this.loadCategories(typeId);
    }
  }

  private loadCategories(typeId: number): void {
    this.categoryService.getCategoriesByType(typeId).subscribe(res => {
      this.categories = res.body ?? [];
    });
  }

  // =====================================================
  // CATEGORY CHANGE → LOAD SUBCATEGORIES
  // =====================================================
  onCategoryChange(): void {
    const categoryId = Number(this.editForm.get('categoryId')!.value);

    this.subcategories = [];
    this.editForm.get('subcategoryId')!.setValue(null);

    if (categoryId) {
      this.loadSubcategories(categoryId);
    }
  }

  private loadSubcategories(categoryId: number): void {
    this.subcategoryService.getSubcategoriesByCategory(categoryId).subscribe(res => {
      this.subcategories = res.body ?? [];
    });
  }

  // =====================================================
  // SAVE
  // =====================================================
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
    result.pipe(finalize(() => (this.isSaving = false))).subscribe({
      next: () => this.previousState(),
      error: () => {},
    });
  }

  previousState(): void {
    window.history.back();
  }

  // =====================================================
  // FORM UPDATE
  // =====================================================
  protected updateForm(machine: IMachine): void {
    this.machine = machine;
    this.machineFormService.resetForm(this.editForm, machine);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, machine.user);
  }
}
