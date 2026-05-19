import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse } from 'app/core/models/api-response.model';
import { IVehicleDocument, IVehicleDocumentResponse } from '../vehicleDocument.model';

@Injectable({ providedIn: 'root' })
export class VehicleDocumentService {
  protected resourceUrl = '/api/vehicle-documents';

  constructor(private http: HttpClient) {}

  // Get documents by machineId
  getMachineDocuments(machineId: number): Observable<IVehicleDocumentResponse> {
    return this.http.get<ApiResponse<IVehicleDocumentResponse>>(`${this.resourceUrl}/machine/${machineId}`).pipe(map(res => res.data));
  }

  // Get all documents (admin)
  getAllDocuments(): Observable<IVehicleDocumentResponse[]> {
    return this.http.get<ApiResponse<IVehicleDocumentResponse[]>>(`${this.resourceUrl}/all`).pipe(map(res => res.data));
  }

  // Upload bulk documents
  uploadDocuments(machineId: number, uploadedBy: number | null, files: File[]): Observable<IVehicleDocumentResponse> {
    const formData = new FormData();

    formData.append('machineId', machineId.toString());

    if (uploadedBy !== null && uploadedBy !== undefined) {
      formData.append('uploadedBy', uploadedBy.toString());
    }

    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i], files[i].name);
    }

    return this.http.post<ApiResponse<IVehicleDocumentResponse>>(`${this.resourceUrl}/bulk-upload`, formData).pipe(map(res => res.data));
  }

  // Find single document by id (for View & Edit pages)
  find(id: number): Observable<HttpResponse<IVehicleDocument>> {
    return this.http
      .get<ApiResponse<IVehicleDocument>>(`${this.resourceUrl}/${id}`, {
        observe: 'response',
      })
      .pipe(
        map(res =>
          res.clone({
            body: res.body?.data ?? null,
          }),
        ),
      );
  }

  // Delete document
  deleteDocument(id: number): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.resourceUrl}/${id}`);
  }
}
