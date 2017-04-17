import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {Marker} from '../../typings/marker';
import {StatechangeEvent} from '../../typings/statechange-event';
import {WalkerMarkerModal} from '../walker-marker-modal/walker-marker-modal'
import {Options} from '../../typings/options';
import {UserOptions} from '../../typings/user-options';
import {OutOfBoundsMarker} from '../../typings/out-of-bounds-marker';

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
  public query: string;
  public results: any;
  public searchValue: string;
  public startLongitude: number;
  public startLatitude: number;
  public stairs: number;
  public elevation: number;
  public wilderness: number;
  public building: number;
  public grass: number;
  public parkingLots: number;
  public preferDesignatedPaths: number;
  public stairsMarkers: Marker[];
  public directionMarkers: Marker[];
  public borders: Marker[];
  public northeast: Marker;
  public southwest: Marker;
  public northwest: Marker;
  public southeast: Marker;
  public start: string;
  public end: string;

  beforeRegister(): void {
    this.is = 'walker-map';
    this.properties = {
      'results': {
        observer: 'resultsChange'
      }
    };
  }

  resultsChange(): void {
    if(this.results && this.results[0]) {
      this.startLongitude = this.results[0].longitude;
      this.startLatitude = this.results[0].latitude;
    }

  }
  search(): void {
    this.query = this.searchValue;
  }
  async cancel(): Promise<void> {
    await Actions.initMarkers(this, 'getMarkers');
    Actions.setStartMarker(this, null);
    Actions.setEndMarker(this, null);
  }

  ready(): void {
    // this.initMarkers();
    this.startLongitude = (-111.643854 + -111.657854) / 2;
    this.startLatitude  = (40.2519803 + 40.244803) / 2;
    this.startPointButtonText = 'Set start marker';
    this.endPointButtonText = 'Set end marker';
    this.southwest = {
      latitude: 40.244803,
      longitude: -111.657854
    };
    this.northeast = {
      latitude: 40.2519803,
      longitude: -111.643854
    };
    this.northwest = {
      latitude: this.northeast.latitude,
      longitude: this.southwest.longitude
    };

    this.southeast = {
      latitude: this.southwest.latitude,
      longitude: this.northeast.longitude
    };
  }
  /**
   * This is needed here because the html calls it as well.
   */
  async initMarkers(): Promise<void> {
    try {
        await Actions.initMarkers(this, 'getMarkers');
    } catch(error) {
      console.error(error.message);
    }

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
      // Actions.setStartMarker(this, null);
      // Actions.setEndMarker(this, null);
      // await Actions.initMarkers(this, 'getMarkers');
      // Actions.setMarkers(this, [...this.markers, marker]);
      // Actions.setLatitudeAndLongitude(this, marker);
      // const walkerMarkerModal: WalkerMarkerModal = this.querySelector('#walker-marker-modal');
      // walkerMarkerModal.open();
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
    this.go();
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
    this.go();
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
        const building: boolean = !!(oldMarker.closingTime || oldMarker.openingTime || oldMarker.title);
        const isStairs: boolean = !building && !oldMarker.buildingId;
        const newMarker: Marker = {
          ...oldMarker,
          latitude,
          longitude,
          building,
          isStairs
        };
        Actions.POST('setMarker', JSON.stringify(newMarker));
        Actions.initMarkers(this, 'getMarkers');
        this.successMessage = '';
        this.successMessage = 'Marker set at new location. Please wait 30 seconds for it to take effect.';
      }

    } catch(error) {
      this.errorMessage = '';
      this.errorMessage = error.message;
    }

  }

  /**
   * Called from dom
   */
  async go(): Promise<void> {
    try {
      const userOptions: UserOptions = {
        stairs: this.stairs || 0,
        elevation: this.elevation || 0,
        wilderness: this.wilderness || 0,
        building: this.building || 0,
        grass: this.grass || 0,
        parkingLots: this.parkingLots || 0,
        preferDesignatedPaths: this.preferDesignatedPaths || 0
      };
      if(this.isOutOfBounds(this.getStartMarker()) || this.isOutOfBounds(this.getEndMarker())) {
        this.querySelector('#directions').map = this.querySelector('#map').map;
        this.start=`${this.getStartMarker().latitude},${this.getStartMarker().longitude}`
        this.end= `${this.getEndMarker().latitude}, ${this.getEndMarker().longitude}`
        Actions.setStartMarker(this, null);
        Actions.setEndMarker(this, null);
        Actions.setDirectionMarkers(this, null);
      } else if (!!this.getStartMarker() && !!this.getEndMarker()){
        if(this.querySelector('#directions')) {
          this.querySelector('#directions').map = null;
        }
        await Actions.travel(this, 'travel', this.getStartMarker(), this.getEndMarker(), userOptions);
      }


    } catch(error) {
      console.error(error);
      this.errorMessage = '';
      this.errorMessage = error.message
    }

  }
  private isOutOfBounds(marker: Marker): boolean {
    try {
      if(marker.longitude < this.southwest.longitude
      || marker.latitude < this.southwest.latitude
      || marker.latitude > this.northeast.latitude
      || marker.longitude > this.northeast.longitude) {
        return true;
      }
      return false;
    } catch(error) {
      console.error(error);
    }


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
    const base: string = 'http://127.0.0.1:3000/markers/';
    if(marker.buildingId) {
      return base + 'yellow_marker.png';
    } else if(marker.isStairs) {
      return base + 'blue_marker.png';
    } else {
      return base + 'red_marker.png';
    }
  }

  /**
   * Called from dom
   */
  computeTitle(title: string): string {
    return title ? title : 'entrance';
  }



  mapStateToThis(e: any): void {
    const state: State = e.detail.state
    this.markers = state.markers;
    this.startMarkers = state.startMarker ? [state.startMarker] : [];
    this.endMarkers = state.endMarker ? [state.endMarker] : [];
    this.displayGoButton = this.getStartMarker() && this.getEndMarker() && !this.settingEndMarker;
    this.displayCancelButton = !!(this.getStartMarker() && this.getEndMarker());
    this.stairs = state.stairs;
    this.elevation = state.elevation;
    this.wilderness = state.wilderness;
    this.grass = state.grass;
    this.building = state.building;
    this.parkingLots = state.parkingLots;
    this.preferDesignatedPaths = state.preferDesignatedPaths;
    this.directionMarkers = state.directionMarkers;
    if(this.directionMarkers) {
      const markers: Marker[] = JSON.parse(localStorage.getItem('directionMarkers'));
      if(this.directionMarkers.length !== markers.length) {
        return;
      }
      for(var i = 0; i < markers.length; i++) {
        if(this.directionMarkers[i].latitude !== markers[i].latitude ||
          this.directionMarkers[i].longitude !== markers[i].longitude) {
          console.error('this marker got truncated ', this.directionMarkers[i]);
        }
      }
    }
  }

}

Polymer(WalkerMap);
