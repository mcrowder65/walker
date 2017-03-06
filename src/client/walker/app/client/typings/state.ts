import {Marker} from './marker';

export interface State {
  markers: Marker[];
  currentMarker: Marker;
  startMarker: Marker;
  endMarker: Marker;
}
