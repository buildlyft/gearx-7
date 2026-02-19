import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMachineOperator } from '../machineOperator.model';
import { MachineOperatorService } from '../service/machineOperator.service';

@Component({
  standalone: true,
  templateUrl: './machineOperator-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MachineOperatorDeleteDialogComponent {
  @Input() machineOperator?: IMachineOperator;

  constructor(
    protected machineOperatorService: MachineOperatorService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.machineOperatorService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
