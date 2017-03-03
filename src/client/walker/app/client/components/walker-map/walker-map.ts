import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {Marker} from '../../typings/marker';
import {StatechangeEvent} from '../../typings/statechange-event';
import {WalkerMarkerModal} from '../walker-marker-modal/walker-marker-modal'
import {Options} from '../../typings/options';

export class WalkerMap {
  public is: string;
  public username: string;
  public querySelector: any;
  public markers: Marker[];
  public action: Action;
  public successMessage: string;
  public errorMessage: string;

  beforeRegister(): void {
    this.is = 'walker-map';
  }

  ready(): void {
    this.initMarkers();
  }

  /**
   * This is needed here because the html calls it as well.
   */
  initMarkers(): void {
    Actions.initMarkers(this, 'getMarkers');
  }

  async clearMarkers(): Promise<void> {
    Actions.setMarkers(this, []);
  }

  async mapClicked(e: any): Promise<void> {
    const latitude: number = e.detail.latLng.lat();
    const longitude: number = e.detail.latLng.lng();
    const marker: Marker = {
      latitude,
      longitude
    }
    Actions.setLatitudeAndLongitude(this, marker);
    const walkerMarkerModal: WalkerMarkerModal = this.querySelector('#walker-marker-modal');
    walkerMarkerModal.open();

  }

  async editMarker(e: any): Promise<void> {
    const marker: any = e.model.__data__.item;
    Actions.setCurrentMarker(this, marker);
    const walkerMarkerModal: WalkerMarkerModal = this.querySelector('#walker-marker-modal');
    walkerMarkerModal.open();
  }

  async markerDragDone(e: any): Promise<void> {
    try {
      const oldMarker: any = e.model.__data__.item;
      const latitude: number = e.detail.latLng.lat();
      const longitude: number = e.detail.latLng.lng();
      const newMarker: Marker = {
        ...oldMarker,
        latitude,
        longitude
      };
      Actions.POST('setMarker', JSON.stringify(newMarker));
      Actions.initMarkers(this, 'getMarkers');
      this.successMessage = '';
      this.successMessage = 'Marker set at new location.';
    } catch(error) {
      this.errorMessage = '';
      this.errorMessage = error.message;
    }

  }

  mapStateToThis(e: any): void {
    const state: State = e.detail.state
    this.markers = state.markers;
  }

}

Polymer(WalkerMap);
