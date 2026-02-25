export interface IVehicleDocument {
  id?: number;
  docType?: string | null;
  fileName?: string | null;
  fileUrl?: string | null;
  contentType?: string | null;
  size?: number | null;
  uploadedAt?: string | null;
  uploadedBy?: string | null;
}

export interface IVehicleDocumentResponse {
  machineId?: number;
  documents?: IVehicleDocument[] | null;
}

export type NewVehicleDocument = Omit<IVehicleDocument, 'id'> & {
  id: null;
};
