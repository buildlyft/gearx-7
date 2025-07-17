import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBooking } from '../booking.model';
import { BookingService } from '../service/booking.service';

@Component({
  standalone: true,
  templateUrl: './booking-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BookingDeleteDialogComponent {
  booking?: IBooking;

  constructor(
    protected bookingService: BookingService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookingService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
