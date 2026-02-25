import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { VehicleDocumentService } from '../service/vehicleDocument.service';

@Component({
  selector: 'jhi-vehicle-document-delete-dialog',
  templateUrl: './vehicleDocument-delete-dialog.component.html',
})
export class VehicleDocumentDeleteDialogComponent {
  @Input() id?: number;

  constructor(
    protected vehicleDocumentService: VehicleDocumentService,
    protected activeModal: NgbActiveModal,
  ) {}

  confirmDelete(): void {
    if (!this.id) {
      this.activeModal.dismiss();
      return;
    }

    this.vehicleDocumentService.deleteDocument(this.id).subscribe({
      next: () => this.activeModal.close('deleted'),
      error: () => this.activeModal.dismiss(),
    });
  }

  cancel(): void {
    this.activeModal.dismiss();
  }
}
