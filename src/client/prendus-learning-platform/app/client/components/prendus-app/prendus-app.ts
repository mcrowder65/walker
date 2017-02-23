import {FirebaseService} from '../../node_modules/prendus-services/services/firebase-service';
import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';

class PrendusApp {
  public is: string;
  public username: string;
  public rootReducer: (state: State, action: Action) => State;

  beforeRegister() {
    this.is = 'prendus-app';
  }

  mapStateToThis(e) {
    const state = e.detail.state
    this.username = state.currentUser.email;
  }

  ready(){
      if (window.PRENDUS_ENV === 'production') {
          FirebaseService.init('AIzaSyAKxLCb9pQdng5_1qi6SGnv4YVdkuO_iG4', 'prendus-production.firebaseapp.com', 'https://prendus-production.firebaseio.com', 'prendus-production.appspot.com', 'prendus-production');
      }
      else {
          FirebaseService.init('AIzaSyBv1mFan0M_QmBhQ7Hkgd0McMidMJtNFRg', 'prendus-development.firebaseapp.com', 'https://prendus-development.firebaseio.com', 'prendus-development.appspot.com', 'prendus-development');
      }

    this.rootReducer = rootReducer;
  }
}

Polymer(PrendusApp);
