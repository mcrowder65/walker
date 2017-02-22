import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';

class PrendusApp {
  public is: string;
  public rootReducer: (state: State, action: Action) => State;

  beforeRegister() {
    this.is = 'prendus-app';
  }

  mapStateToThis(e: any) {
    const state = e.detail.state
  }

  ready() {
    this.rootReducer = rootReducer;
  }
}

Polymer(PrendusApp);
