import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';

export class WalkerMarkerModal {
  public is: string;
  public rootReducer: (state: State, action: Action) => State;
  public querySelector: any;

  beforeRegister(): void {
    this.is = 'walker-marker-modal';
  }

  ready(): void {
    this.rootReducer = rootReducer;
  }
  open(): void {
    this.querySelector('#modal').open();
  }
  mapStateToThis(e: any): void {
    const state: State = e.detail.state
  }

}

Polymer(WalkerMarkerModal);
