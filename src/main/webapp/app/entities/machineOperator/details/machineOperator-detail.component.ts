import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IMachineOperator } from '../machineOperator.model';

@Component({
  standalone: true,
  selector: 'jhi-machine-operator-detail',
  templateUrl: './machineOperator-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class MachineOperatorDetailComponent {
  @Input() machineOperator: IMachineOperator | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {
    this.activatedRoute.data.subscribe(({ machineOperator }) => {
      this.machineOperator = machineOperator;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
