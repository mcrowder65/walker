import {Marker} from './marker';

export interface Action {
  type: 'SET_MARKERS' |
        'SET_LATITUDE_AND_LONGITUDE' |
        'RESET_MARKER_MODAL' |
        'SET_CURRENT_MARKER' |
        'SET_START_MARKER' |
        'SET_END_MARKER';
  markers?: Marker[];
  currentMarker?: Marker;
  startMarker?: Marker;
  endMarker?: Marker;
}
