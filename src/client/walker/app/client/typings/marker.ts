export interface Marker {
  latitude: number;
  longitude: number;
  title?: string;
  id?: string;
  openingTime?: string;
  closingTime?: string;
  building?: boolean;
  buildingId?: string;
  entrances?: { [entranceId: string]: string } | string[];
  isStairs?: boolean;
}
