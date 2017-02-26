import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {Marker} from '../../typings/marker';

export class WalkerApp {
  public is: string;
  beforeRegister(): void {
    this.is = 'walker-app';
  }
}

Polymer(WalkerApp);
