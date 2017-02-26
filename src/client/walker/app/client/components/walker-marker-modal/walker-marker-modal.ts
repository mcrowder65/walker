import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {StatechangeEvent} from '../../typings/statechange-event';
import {Marker} from '../../typings/marker';

export class WalkerMarkerModal {
  public is: string;
  public querySelector: any;
  public latitude: number;
  public longitude: number;
  public action: Action;
  public title: string;
  public markerId: string;

  beforeRegister(): void {
    this.is = 'walker-marker-modal';
  }

  open(): void {
    this.querySelector('#modal').open();
  }

  async setMarker(): Promise<void> {
    const marker: Marker = {
      latitude: this.latitude,
      longitude: this.longitude,
      title: this.title,
      id: this.markerId || ''
    };

    const ajax = this.querySelector('#setMarkerAjax');

    ajax.body = {
      latitude: marker.latitude,
      longitude: marker.longitude,
      title: marker.title,
      id: marker.id
    };

    const request = ajax.generateRequest();
    await request.completes;
    const response = request.response;


    const getMarkerAjax = document.querySelector('#walker-map');
    getMarkerAjax.initMarkers();//TODO abstract this...

  }

  mapStateToThis(e: StatechangeEvent): void {
    const state: State = e.detail.state
    this.latitude = state.currentClickLatitude;
    this.longitude = state.currentClickLongitude;
    this.markerId = state.currentMarkerId;
  }

}

Polymer(WalkerMarkerModal);
