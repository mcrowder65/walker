import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';

class WalkerApp {
  public is: string;
  public username: string;
  public rootReducer: (state: State, action: Action) => State;

  beforeRegister(): void {
    this.is = 'walker-app';
  }

  mapStateToThis(e: any): void {
    const state: State = e.detail.state
  }

  ready(): void {
    this.rootReducer = rootReducer;
  }
}

Polymer(WalkerApp);
