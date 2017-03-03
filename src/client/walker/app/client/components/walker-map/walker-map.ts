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
  public setStartMarkerMessage: 'SET YOUR START MARKER!!!!' | '' | 'NOW SET YOUR END MARKER!!!';
  public settingStartMarker: boolean;
  public settingEndMarker: boolean;
  public startPointButtonText: 'Don\'t set your start marker' | 'Set start marker';
  public properties: any;


  beforeRegister(): void {
    this.is = 'walker-map';
    this.properties = {
      settingStartMarker: {
        observer: 'settingStartMarkerChange'
      }
    };
  }

  ready(): void {
    this.initMarkers();
    this.startPointButtonText = 'Set start marker';
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
  settingStartMarkerChange(): void {
      if(this.settingStartMarker) {
        this.setStartMarkerMessage = 'SET YOUR START MARKER!!!!';
        this.startPointButtonText = 'Don\'t set your start marker';
      } else {
        this.setStartMarkerMessage = '';
        this.startPointButtonText = 'Set start marker';
        Actions.initMarkers(this, 'getMarkers');
      }
  }

  async mapClicked(e: any): Promise<void> {
    const latitude: number = e.detail.latLng.lat();
    const longitude: number = e.detail.latLng.lng();
    const marker: Marker = {
      latitude,
      longitude
    };
    if(this.settingStartMarker) {
      const markersString = await Actions.POST('getMarkers');
      let markers = JSON.parse(markersString);
      for(let i: number = 0; i < markers.length; i++) {
        markers[i] = JSON.parse(markers[i]);
      }

      Actions.setMarkers(this, [...this.markers, marker]);
      this.setStartMarkerMessage = 'NOW SET YOUR END MARKER!!!';

    } else {

      Actions.setLatitudeAndLongitude(this, marker);
      const walkerMarkerModal: WalkerMarkerModal = this.querySelector('#walker-marker-modal');
      walkerMarkerModal.open();
    }


  }

  async editMarker(e: any): Promise<void> {
    const marker: any = e.model.__data__.item;
    Actions.setCurrentMarker(this, marker);
    const walkerMarkerModal: WalkerMarkerModal = this.querySelector('#walker-marker-modal');
    walkerMarkerModal.open();
  }

  async setStartMarker(e: any): Promise<void> {
    this.settingStartMarker = !this.settingStartMarker;
  }

  async markerDragDone(e: any): Promise<void> {
    try {
      const oldMarker: any = e.model.__data__.item;
      const latitude: number = e.detail.latLng.lat();
      const longitude: number = e.detail.latLng.lng();
      if(!this.settingStartMarker) {
        const building: boolean = oldMarker.closingTime !== undefined
                               || oldMarker.openingTime !== undefined
                               || oldMarker.title !== undefined;
        const newMarker: Marker = {
          ...oldMarker,
          latitude,
          longitude,
          building
        };
        Actions.POST('setMarker', JSON.stringify(newMarker));
        Actions.initMarkers(this, 'getMarkers');
        this.successMessage = '';
        this.successMessage = 'Marker set at new location.';
      }

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
