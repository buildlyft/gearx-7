import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import { IType } from '../type.model';

@Component({
  standalone: true,
  selector: 'jhi-type-detail',
  templateUrl: './type-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class TypeDetailComponent {
  @Input() type: IType | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {
    this.activatedRoute.data.subscribe(({ type }) => {
      this.type = type;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
