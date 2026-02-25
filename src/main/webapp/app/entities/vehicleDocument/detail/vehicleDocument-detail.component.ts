import { RouterModule } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import { IVehicleDocument } from '../vehicleDocument.model';

@Component({
  selector: 'jhi-vehicle-document-detail',
  templateUrl: './vehicleDocument-detail.component.html',
  standalone: true,
  imports: [SharedModule, RouterModule],
})
export class VehicleDocumentDetailComponent implements OnInit {
  document: IVehicleDocument | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vehicleDocument }) => {
      this.document = vehicleDocument ?? null;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
