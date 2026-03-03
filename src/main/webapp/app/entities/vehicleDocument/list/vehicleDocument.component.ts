import { RouterModule } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { take } from 'rxjs/operators';
import { AccountService } from 'app/core/auth/account.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import SharedModule from 'app/shared/shared.module';

import { IVehicleDocument } from '../vehicleDocument.model';
import { VehicleDocumentService } from '../service/vehicleDocument.service';
import { VehicleDocumentDeleteDialogComponent } from '../delete/vehicleDocument-delete-dialog.component';

@Component({
  selector: 'jhi-vehicle-document',
  templateUrl: './vehicleDocument.component.html',
  standalone: true,
  imports: [SharedModule, RouterModule],
})
export class VehicleDocumentComponent implements OnInit {
  documents: IVehicleDocument[] = [];
  isLoading = false;
  isAdminOrPartner = false;

  constructor(
    private vehicleDocumentService: VehicleDocumentService,
    private accountService: AccountService,
    private modalService: NgbModal,
  ) {}

  ngOnInit(): void {
    // Production-safe role check (auto complete subscription)
    this.accountService
      .getAuthenticationState()
      .pipe(take(1))
      .subscribe(account => {
        this.isAdminOrPartner = !!(account?.authorities?.includes('ROLE_ADMIN') || account?.authorities?.includes('ROLE_PARTNER'));
      });

    this.loadAll();
  }

  loadAll(): void {
    this.isLoading = true;

    this.vehicleDocumentService.getAllDocuments().subscribe({
      next: res => {
        this.documents = (res ?? []).flatMap(r =>
          (r.documents ?? []).map(doc => ({
            ...doc,
            machineId: r.machineId,
          })),
        );
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  delete(id?: number): void {
    if (!id) return;

    const modalRef = this.modalService.open(VehicleDocumentDeleteDialogComponent, {
      size: 'lg',
      backdrop: 'static',
    });

    modalRef.componentInstance.id = id;

    modalRef.closed.pipe(take(1)).subscribe(() => {
      this.loadAll();
    });
  }

  trackId(index: number, item: IVehicleDocument): number {
    return item.id ?? index;
  }
}
