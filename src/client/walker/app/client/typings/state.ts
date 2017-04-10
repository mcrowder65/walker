import {Marker} from './marker';

export interface State {
  markers?: Marker[];
  currentMarker?: Marker;
  startMarker?: Marker;
  endMarker?: Marker;
  stairs?: number;
  elevation?: number;
  wilderness?: number;
  grass?: number;
  building?: number;
  parkingLots?: number;
  preferDesignatedPaths?: number;
}
