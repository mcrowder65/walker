import {Marker} from './marker';

export interface Action {
  type: 'SET_MARKERS' | 'SET_LATITUDE_AND_LONGITUDE';
  markers?: Marker[];
  currentClickLatitude?: number;
  currentClickLongitude?: number;
}
