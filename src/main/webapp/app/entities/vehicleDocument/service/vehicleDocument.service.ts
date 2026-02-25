import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { IVehicleDocument, IVehicleDocumentResponse } from '../vehicleDocument.model';

@Injectable({ providedIn: 'root' })
export class VehicleDocumentService {
  protected resourceUrl = '/api/vehicle-documents';

  constructor(private http: HttpClient) {}

  // Get documents by machineId
  getMachineDocuments(machineId: number): Observable<IVehicleDocumentResponse> {
    return this.http.get<IVehicleDocumentResponse>(`${this.resourceUrl}/machine/${machineId}`);
  }

  // Get all documents (admin)
  getAllDocuments(): Observable<IVehicleDocumentResponse[]> {
    return this.http.get<IVehicleDocumentResponse[]>(`${this.resourceUrl}/all`);
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

    return this.http.post<IVehicleDocumentResponse>(`${this.resourceUrl}/bulk-upload`, formData);
  }

  // Find single document by id (for View & Edit pages)
  find(id: number): Observable<HttpResponse<IVehicleDocument>> {
    return this.http.get<IVehicleDocument>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  // Delete document
  deleteDocument(id: number): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/${id}`);
  }
}
