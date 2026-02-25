import { RouterModule } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { IMachine } from '../../machine/machine.model';
import { MachineService } from '../../machine/service/machine.service';
import { ActivatedRoute, Router } from '@angular/router';
import { VehicleDocumentService } from '../service/vehicleDocument.service';
import { FormsModule } from '@angular/forms';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-vehicle-document-update',
  templateUrl: './vehicleDocument-update.component.html',
  standalone: true,
  imports: [SharedModule, FormsModule, RouterModule],
})
export class VehicleDocumentUpdateComponent implements OnInit {
  machineId: number | null = null;
  machines: IMachine[] = [];
  selectedFiles: File[] = [];
  isSaving = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private vehicleDocumentService: VehicleDocumentService,
    private machineService: MachineService,
  ) {}

  ngOnInit(): void {
    //  Load all machines from backend
    this.machineService.query().subscribe({
      next: res => {
        this.machines = res.body ?? [];
      },
      error: () => {
        this.machines = [];
      },
    });

    // If machineId passed in route (optional)
    this.activatedRoute.params.subscribe(params => {
      if (params['machineId']) {
        this.machineId = +params['machineId'];
      }
    });
  }

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files) return;

    for (let i = 0; i < input.files.length; i++) {
      const file = input.files.item(i);
      if (file) {
        // Prevent duplicates
        const alreadyExists = this.selectedFiles.some(f => f.name === file.name && f.size === file.size);

        if (!alreadyExists) {
          this.selectedFiles.push(file);
        }
      }
    }

    // Reset input so same file can be selected again
    input.value = '';
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  save(): void {
    if (!this.machineId || this.selectedFiles.length === 0) {
      return;
    }

    this.isSaving = true;

    this.vehicleDocumentService.uploadDocuments(this.machineId, null, this.selectedFiles).subscribe({
      next: () => {
        this.isSaving = false;
        this.router.navigate(['/vehicle-document']);
      },
      error: () => {
        this.isSaving = false;
      },
    });
  }

  previousState(): void {
    window.history.back();
  }
}
