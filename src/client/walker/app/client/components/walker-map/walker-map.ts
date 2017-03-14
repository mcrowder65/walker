import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {Marker} from '../../typings/marker';
import {StatechangeEvent} from '../../typings/statechange-event';
import {WalkerMarkerModal} from '../walker-marker-modal/walker-marker-modal'
import {Options} from '../../typings/options';
import {UtilitiesService} from '../../services/utilities-service';

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
  public displayGoButton: boolean;
  public displayCancelButton: boolean;
  public $: any;
  public startMarkers: Marker[];
  public endMarkers: Marker[];

  beforeRegister(): void {
    this.is = 'walker-map';
  }

  async cancel(): Promise<void> {
    await Actions.initMarkers(this, 'getMarkers');
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
      Actions.setStartMarker(this, marker);
    } else if(this.settingEndMarker) {
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
      Actions.setEndMarker(this, this.getEndMarker());
    }

  }

  /**
   * Called from dom
   */
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

  /**
   * Called from dom
   */
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

  /**
   * Called from dom
   */
  async markerDragDone(e: any): Promise<void> {
    try {
      if(this.settingEndMarker || this.settingStartMarker) {
        this.errorMessage = '';
        this.errorMessage = 'You need to set the start or end marker.';
        await Actions.initMarkers(this, 'getMarkers');
        return;
      }
      const oldMarker: Marker = e.model.__data__.item;
      const latitude: number = e.detail.latLng.lat();
      const longitude: number = e.detail.latLng.lng();
      if(!this.settingStartMarker) {
        const building: boolean = UtilitiesService.isDefined(oldMarker.closingTime)
                               || UtilitiesService.isDefined(oldMarker.openingTime)
                               || UtilitiesService.isDefined(oldMarker.title);
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

  /**
   * Called from dom
   */
  go(): void {
    Actions.travel(this, 'travel', this.getStartMarker(), this.getEndMarker());
  }

  private getStartMarker(): Marker {
    return this.startMarkers.length === 1 ? this.startMarkers[0] : null;
  }

  private getEndMarker(): Marker {
    return this.endMarkers.length === 1 ? this.endMarkers[0] : null;
  }

  /**
   * Called from dom
   */
  computeIcon(marker: Marker): string {
    const base: string = 'http://localhost:8000/markers/';
    if(UtilitiesService.isDefined(marker.buildingId)) {
      return base + 'yellow_marker.png';
    } else {
      return base + 'red_marker.png';
    }
  }

  mapStateToThis(e: any): void {
    const state: State = e.detail.state
    this.markers = state.markers;
    this.startMarkers = UtilitiesService.isDefined(state.startMarker) ? [state.startMarker] : [];
    this.endMarkers = UtilitiesService.isDefined(state.endMarker) ? [state.endMarker] : [];
    this.displayGoButton = UtilitiesService.isDefined(this.getStartMarker())
                          && UtilitiesService.isDefined(this.getEndMarker())
                          && !this.settingEndMarker;
    this.displayCancelButton = UtilitiesService.isDefined(this.getStartMarker())
                            || UtilitiesService.isDefined(this.getEndMarker());
  }

}

Polymer(WalkerMap);
