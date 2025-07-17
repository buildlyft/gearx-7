import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMachine } from '../machine.model';
import { MachineService } from '../service/machine.service';

@Component({
  standalone: true,
  templateUrl: './machine-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MachineDeleteDialogComponent {
  machine?: IMachine;

  constructor(
    protected machineService: MachineService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.machineService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
