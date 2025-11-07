import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISubcategory } from '../subcategory.model';
import { SubcategoryService } from '../service/subcategory.service';

@Component({
  standalone: true,
  templateUrl: './subcategory-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SubcategoryDeleteDialogComponent {
  subcategory?: ISubcategory;

  constructor(
    protected subcategoryService: SubcategoryService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.subcategoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
