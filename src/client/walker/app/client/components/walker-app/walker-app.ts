import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {Marker} from '../../typings/marker';

export class WalkerApp {
  public is: string;
  public rootReducer: (state: State, action: Action) => State;

  beforeRegister(): void {
    this.is = 'walker-app';
  }
  
  ready(): void {
    this.rootReducer = rootReducer;
  }
}

Polymer(WalkerApp);
