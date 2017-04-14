import {Marker} from './marker';
import {OutOfBoundsMarker} from './out-of-bounds-marker';

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
  directionMarkers?: Marker[];
  outOfBoundsDirections?: OutOfBoundsMarker[];
}
