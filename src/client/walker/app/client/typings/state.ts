import {Marker} from './marker';

export interface State {
  markers?: Marker[];
  currentMarker?: Marker;
  startMarker?: Marker;
  endMarker?: Marker;
  stairs?: number;
  elevation?: number;
  wilderness?: boolean;
  grass?: boolean;
  building?: boolean;
  parkingLots?: boolean;
}
