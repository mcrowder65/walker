import {StatechangeEvent} from '../../typings/statechange-event';
import {Actions} from '../../redux/actions';
import {UtilitiesService} from '../../node_modules/prendus-services/services/utilities-service';

class PrendusRouter {
  public is: string;
  public username: string;
  public loggedIn: 'true' | 'false';
  public mainViewToShow: 'routes' | 'spinner';
  public observers: string[];
  public querySelector: any;

  beforeRegister() {
    this.is =  "prendus-router";
  }

  ready(): void {
    Actions.defaultAction(this);
  }

  mapStateToThis(e: StatechangeEvent): void {
      const state = e.detail.state;
      this.loggedIn = 'false';
      this.mainViewToShow = state.mainViewToShow;
  }
}

Polymer(PrendusRouter);
