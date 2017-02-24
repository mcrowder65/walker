import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';

class PrendusApp {
  public is: string;
  public username: string;
  public rootReducer: (state: State, action: Action) => State;
  public apiKey: string;
  public querySelector: any;

  beforeRegister() {
    this.is = 'prendus-app';
  }

  mapStateToThis(e: any) {
    const state = e.detail.state
    this.username = state.currentUser.email;
  }

  ready() {
    this.rootReducer = rootReducer;
    this.apiKey = 'AIzaSyB7zQeZeCZFWzwupwYjbioQYldZkdF3oPk';

  }
  tap() {
    console.log('tap');
    const map = this.querySelector('#gmap');
    console.log(map);
    map.clear();
    map.notifyResize();

  }
}

Polymer(PrendusApp);
