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
  public isBuilding: boolean;

  beforeRegister(): void {
    this.is = 'walker-marker-modal';
    this.isBuilding = false;
  }

  thisIsABuilding(): void {
    this.isBuilding = true;
  }

  thisIsNotABuilding(): void {
    this.isBuilding = false;
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
        closingTime: this.closingTime
      };

      Actions.POST('setMarker', JSON.stringify(marker));
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
      const ajax = this.querySelector('#deleteMarkerAjax');
      const marker: Marker = {
        latitude: this.latitude,
        longitude: this.longitude,
        id: this.markerId,
        title: this.title,
        openingTime: this.openingTime,
        closingTime: this.closingTime
      };

      await Actions.deleteMarker(marker, ajax);

      const getMarkerAjax = this.querySelector('#getMarkersAjax');
      await Actions.initMarkersWithAjax(this, getMarkerAjax);
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
    }
  }

}

Polymer(WalkerMarkerModal);
