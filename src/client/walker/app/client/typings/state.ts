import {Marker} from './marker';

export interface State {
  markers: Marker[];
  currentClickLatitude: number;
  currentClickLongitude: number;
}
