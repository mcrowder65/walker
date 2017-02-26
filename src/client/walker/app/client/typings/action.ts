import {Marker} from './marker';

export interface Action {
  type: 'SET_MARKERS' | 'SET_CURRENT_MARKER';
  markers?: Marker[];
  currentMarker?: Marker;
}
