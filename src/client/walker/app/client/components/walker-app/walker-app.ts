import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';

export class WalkerApp {
  public is: string;
  public rootReducer: (state: State, action: Action) => State;

  beforeRegister(): void {
    this.is = 'walker-app';
  }

  ready(): void {
    this.rootReducer = rootReducer;
  }

  mapStateToThis(e: any): void {
    console.log('state change walker-app!');
  }
}

Polymer(WalkerApp);
