import {Marker} from './marker';
import {OutOfBoundsMarker} from './out-of-bounds-marker';

export interface Action {
  type: 'SET_MARKERS' |
        'SET_LATITUDE_AND_LONGITUDE' |
        'RESET_MARKER_MODAL' |
        'SET_CURRENT_MARKER' |
        'SET_START_MARKER' |
        'SET_END_MARKER' |
        'SET_STAIRS' |
        'SET_ELEVATION' |
        'SET_WILDERNESS' |
        'SET_GRASS' |
        'SET_BUILDING' |
        'SET_PARKING_LOTS' |
        'SET_PREFER_DESIGNATED_PATHS' |
        'SET_DIRECTION_MARKERS' |
        'SET_OUT_OF_BOUND_DIRECTIONS';
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
