import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { TypeService } from '../service/type.service';
import { TypeDeleteDialogComponent } from './type-delete-dialog.component';

describe('Type Delete Component', () => {
  let comp: TypeDeleteDialogComponent;
  let fixture: ComponentFixture<TypeDeleteDialogComponent>;
  let service: TypeService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TypeDeleteDialogComponent],
      providers: [NgbActiveModal],
    }).compileComponents();

    fixture = TestBed.createComponent(TypeDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TypeService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  it('Should call delete service on confirmDelete', fakeAsync(() => {
    jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({})));
    comp.confirmDelete(123);
    tick();
    expect(service.delete).toHaveBeenCalledWith(123);
    expect(mockActiveModal.close).toHaveBeenCalled();
  }));
});
