import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {StatechangeEvent} from '../../typings/statechange-event';
import {Marker} from '../../typings/marker';
import {Options} from '../../typings/options';

export class WalkerMarkerModal {
  public is: string;
  public querySelector: any;
  public latitude: number;
  public longitude: number;
  public action: Action;
  public title: string;
  public markerId: string;
  public openingTime: string;
  public closingTime: string;
  public successMessage: string;
  public errorMessage: string;
  public building: boolean;
  public properties: any;
  public isBuildingSelection: 'yes' | 'no' | 'neither';
  public buildings: Marker[];
  public buildingId: string;

  beforeRegister(): void {
    this.is = 'walker-marker-modal';
    this.properties = {
      building: {
        observer: 'changeIsBuildingSelection'
      }
    }
  }

  thisIsABuilding(): void {
    this.building = true;
  }

  thisIsNotABuilding(): void {
    this.building = false;
  }

  changeIsBuildingSelection(): void {
    this.isBuildingSelection = this.building ? 'yes' : 'no';
  }

  async getBuildings(): Promise<void> {
    const buildings = await Actions.POST('getBuildings');
    const buildingsArray = JSON.parse(buildings);
    for(let i: number = 0; i < buildingsArray.length; i++) {
      buildingsArray[i] = JSON.parse(buildingsArray[i]);
    }
    this.buildings = buildingsArray;
  }

  setBuilding(e: any): void {
    const buildingId: string = e.target.id;
    this.buildingId = buildingId;
  }
  /**
   * This gets called from walker-map
   */
  open(): void {
    this.querySelector('#modal').open();

  }

  async setMarker(): Promise<void> {
    try {
      const marker: Marker = {
        latitude: this.latitude,
        longitude: this.longitude,
        title: this.title,
        id: this.markerId || '',
        openingTime: this.openingTime,
        closingTime: this.closingTime,
        building: this.building,
        buildingId: this.buildingId
      };

      await Actions.POST('setMarker', JSON.stringify(marker));
      await Actions.initMarkers(this, 'getMarkers');
      Actions.resetMarkerModal(this);
      this.successMessage = '';
      this.successMessage = 'Marker set';
    } catch(error) {
      this.errorMessage = '';
      this.errorMessage = error.message;
    }

  }

  async deleteMarker(e: any): Promise<void> {
    try {
      const marker: Marker = {
        latitude: this.latitude,
        longitude: this.longitude,
        id: this.markerId,
        title: this.title,
        openingTime: this.openingTime,
        closingTime: this.closingTime,
        building: this.building
      };
      await Actions.POST('deleteMarker', JSON.stringify(marker));
      await Actions.initMarkers(this, 'getMarkers');
      await Actions.resetMarkerModal(this);
      this.successMessage = '';
      this.successMessage = 'Marker deleted';
    } catch(error) {
      this.errorMessage = '';
      this.errorMessage = error.message;
    }

  }


  mapStateToThis(e: StatechangeEvent): void {
    const state: State = e.detail.state
    if(state.currentMarker) {
      this.latitude = state.currentMarker.latitude;
      this.longitude = state.currentMarker.longitude;
      this.markerId = state.currentMarker.id;
      this.title = state.currentMarker.title;
      this.openingTime = state.currentMarker.openingTime;
      this.closingTime = state.currentMarker.closingTime;
      this.building = this.openingTime !== undefined || this.closingTime !== undefined || this.title !== undefined;
      this.buildingId = !this.building ? state.currentMarker.buildingId : this.buildingId;
      this.getBuildings();
    }
  }

}

Polymer(WalkerMarkerModal);
