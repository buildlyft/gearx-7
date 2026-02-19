import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';

import { IMachineOperator } from '../machineOperator.model';
import { MachineOperatorService } from '../service/machineOperator.service';
import { MachineOperatorDeleteDialogComponent } from '../delete/machineOperator-delete-dialog.component';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

import { MachineService } from 'app/entities/machine/service/machine.service';
import { IMachine } from 'app/entities/machine/machine.model';

@Component({
  standalone: true,
  selector: 'jhi-machine-operator',
  templateUrl: './machineOperator.component.html',
  imports: [SharedModule, RouterModule],
})
export class MachineOperatorComponent implements OnInit {
  machineOperators: IMachineOperator[] = [];
  machines: IMachine[] = [];
  isLoading = false;
  noMachinesAvailable = false;

  constructor(
    protected machineOperatorService: MachineOperatorService,
    protected machineService: MachineService,
    protected modalService: NgbModal,
  ) {}

  ngOnInit(): void {
    this.load();
    this.loadMachines();
  }

  load(): void {
    this.isLoading = true;

    this.machineOperatorService.getAllActive().subscribe({
      next: (res: HttpResponse<IMachineOperator[]>) => {
        this.machineOperators = res.body ?? [];
        this.isLoading = false;
      },
      error: () => (this.isLoading = false),
    });
  }

  loadMachines(): void {
    this.machineService.queryWithoutOperator().subscribe({
      next: (res: HttpResponse<IMachine[]>) => {
        const availableMachines = res.body ?? [];
        this.noMachinesAvailable = availableMachines.length === 0;
      },
    });
  }

  getMachineName(machineId?: number | null): string {
    if (!machineId || this.machines.length === 0) {
      return '';
    }

    const machine = this.machines.find(m => m.id === machineId);

    return machine ? `${machine.brand ?? ''} ${machine.model ?? ''}`.trim() : '';
  }

  delete(operator: IMachineOperator): void {
    const modalRef = this.modalService.open(MachineOperatorDeleteDialogComponent, { size: 'lg', backdrop: 'static' });

    // ✅ PASS FULL OBJECT
    modalRef.componentInstance.machineOperator = operator;

    modalRef.closed.subscribe((reason: string) => {
      if (reason === ITEM_DELETED_EVENT) {
        this.load();
      }
    });
  }
}
