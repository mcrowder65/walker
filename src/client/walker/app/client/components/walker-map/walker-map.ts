import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {Marker} from '../../typings/marker';
import {StatechangeEvent} from '../../typings/statechange-event';
import {WalkerMarkerModal} from '../walker-marker-modal/walker-marker-modal'

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

  async initMarkers(): Promise<void> {
    const ajax = this.querySelector('#getMarkersAjax');
    Actions.initMarkersWithAjax(this, ajax);

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
      const setMarkerAjax = this.querySelector('#setMarkerAjax');
      Actions.setMarker(newMarker, setMarkerAjax);
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
