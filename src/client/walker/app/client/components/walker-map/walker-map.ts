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
  public settingStartMarker: boolean = false;
  public settingEndMarker: boolean = false;
  public startPointButtonText: 'Done setting start marker' | 'Set start marker';
  public endPointButtonText: 'Done setting end marker' | 'Set end marker';
  public properties: any;
  public startMarker: Marker;
  public endMarker: Marker;
  public displayGoButton: boolean;
  public displayCancelButton: boolean;

  beforeRegister(): void {
    this.is = 'walker-map';
  }

  cancel(): void {
    console.log('cancel');
    Actions.initMarkers(this, 'getMarkers');
    Actions.setStartMarker(this, null);
    Actions.setEndMarker(this, null);
  }
  ready(): void {
    this.initMarkers();
    this.startPointButtonText = 'Set start marker';
    this.endPointButtonText = 'Set end marker';
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
    };

    if(this.settingStartMarker) {
      console.log('this.settingStartMarker')
      Actions.setStartMarker(this, marker);
    } else if(this.settingEndMarker) {
      console.log('this.settingEndMarker');
      Actions.setEndMarker(this, marker);
    } else {
      Actions.setStartMarker(this, null);
      Actions.setEndMarker(this, null);
      await Actions.initMarkers(this, 'getMarkers');
      Actions.setMarkers(this, [...this.markers, marker]);
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

  setStartMarker(e: any): void {
    this.settingStartMarker = !this.settingStartMarker;
    if(this.settingStartMarker) {
      this.startPointButtonText = 'Done setting start marker';
    } else {
      this.startPointButtonText = 'Set start marker';
    }
  }

  setEndMarker(e: any): void {
    if(!this.settingStartMarker) {
      this.settingEndMarker = !this.settingEndMarker;
      if(this.settingEndMarker) {
        this.endPointButtonText = 'Done setting end marker';
      } else {
        this.endPointButtonText = 'Set end marker';
      }
    }

  }

  endMarkerDragDone(e: any): void {
    const latitude: number = e.detail.latLng.lat();
    const longitude: number = e.detail.latLng.lng();
    const oldMarker: any = e.model.__data__.item;
    const newMarker: Marker = {
      ...oldMarker,
      latitude,
      longitude
    };
    Actions.setEndMarker(this, newMarker);
  }

  startMarkerDragDone(e: any): void {
    const latitude: number = e.detail.latLng.lat();
    const longitude: number = e.detail.latLng.lng();
    const oldMarker: any = e.model.__data__.item;
    const newMarker: Marker = {
      ...oldMarker,
      latitude,
      longitude
    };
    Actions.setStartMarker(this, newMarker);
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

  go(): void {
    Actions.travel(this, 'travel', this.startMarker, this.endMarker);
  }
  mapStateToThis(e: any): void {
    const state: State = e.detail.state
    this.markers = state.markers;
    this.startMarker = state.startMarker;
    this.endMarker = state.endMarker;
    this.displayGoButton = this.startMarker !== undefined && this.startMarker !== null
                        && this.endMarker !== undefined && this.endMarker !== null;
    this.displayCancelButton = (this.startMarker !== undefined && this.startMarker !== null) ||
                               (this.endMarker !== undefined && this.endMarker !== null);
  }

}

Polymer(WalkerMap);
