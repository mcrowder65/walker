import {Marker} from './marker';

export interface Action {
  type: 'SET_MARKERS';
  markers: Marker[];
}
