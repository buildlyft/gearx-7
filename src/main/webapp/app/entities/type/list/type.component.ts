import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { HttpResponse } from '@angular/common/http';

import SharedModule from 'app/shared/shared.module';
import { SortDirective, SortByDirective } from 'app/shared/sort';

import { IType } from '../type.model';
import { TypeService } from '../service/type.service';
import { TypeDeleteDialogComponent } from '../delete/type-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-type',
  templateUrl: './type.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective],
})
export class TypeComponent implements OnInit {
  types: IType[] = [];
  isLoading = false;

  predicate = 'id';
  ascending = true;

  constructor(
    protected typeService: TypeService,
    protected modalService: NgbModal,
  ) {}

  trackId = (_index: number, item: IType): number => this.typeService.getTypeIdentifier(item);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;

    this.typeService.query().subscribe({
      next: (res: HttpResponse<IType[]>) => {
        this.types = res.body ?? [];
        this.isLoading = false;
      },
      error: () => (this.isLoading = false),
    });
  }

  delete(type: IType): void {
    const modalRef = this.modalService.open(TypeDeleteDialogComponent, {
      size: 'lg',
      backdrop: 'static',
    });

    modalRef.componentInstance.type = type;

    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.load();
      }
    });
  }
}
