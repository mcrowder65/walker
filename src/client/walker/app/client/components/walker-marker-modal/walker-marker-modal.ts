import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {StatechangeEvent} from '../../typings/statechange-event';

class WalkerMarkerModal {
  public is: string;
  public querySelector: any;
  public latitude: number;
  public longitude: number;
  public action: Action;

  beforeRegister(): void {
    this.is = 'walker-marker-modal';
  }

  ready(): void {

    console.log('walker-marker-modal ready');
    console.log(this.rootReducer);
  }

  open(): void {
    this.querySelector('#modal').open();
  }

  mapStateToThis(e: StatechangeEvent): void {
    const state: State = e.detail.state
    this.latitude = state.currentClickLatitude;
    this.longitude = state.currentClickLongitude;
    console.log('walker-marker-modal mapStateToThis');
    console.log(this.latitude + ' ' + this.longitude);
  }

}

Polymer(WalkerMarkerModal);
