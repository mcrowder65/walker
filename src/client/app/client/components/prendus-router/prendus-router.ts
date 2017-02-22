import {StatechangeEvent} from '../../typings/statechange-event';
import {Actions} from '../../redux/actions';
import {UtilitiesService} from '../../node_modules/prendus-services/services/utilities-service';

class PrendusRouter {
  public is: string;
  public username: string;
  public loggedIn: 'true' | 'false';
  public observers: string[];

  beforeRegister() {
    this.is =  "prendus-router";

    this.observers = [
      '_routeChanged(route.*)'
    ];

  }
  async _routeChanged(routeObject: any): Promise<void> {

  }

  mapStateToThis(e: StatechangeEvent): void {
      const state = e.detail.state;
      this.loggedIn = this.username ? 'true' : 'false';
  }
}

Polymer(PrendusRouter);
