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

    this.observers = [
      '_routeChanged(route.*)'
    ];

  }
  async _routeChanged(routeObject: any): Promise<void> {
    if(!this.username || !this.loggedIn) {
      // Call default action to determine if the user is logged in, since
      // this route change function is called before the first mapStateToThis.
      Actions.defaultAction(this);
      await Actions.checkUserAuth(this);
    }
    const route: string = routeObject.value.path;
    switch(route) {

      case '/': {
        if(this.loggedIn === 'true') {
          UtilitiesService.importElement(this, 'components/prendus-homepage/prendus-homepage.html', 'homepage');
        } else {
          UtilitiesService.importElement(this, 'components/prendus-landing/prendus-landing.html', 'landing');
        }

        break;
      }

      case '/login': {
        UtilitiesService.importElement(this, 'components/prendus-login/prendus-login.html', 'login');
        break;
      }

      case '/signup': {
        UtilitiesService.importElement(this, 'components/prendus-create-account/prendus-create-account.html', 'create-account');
        break;
      }

      case '/profile': {
        UtilitiesService.importElement(this, 'components/prendus-profile/prendus-profile.html', 'profile');
        break;
      }

      case '/privacy-policy': {
        UtilitiesService.importElement(this, 'components/prendus-privacy-policy/prendus-privacy-policy.html', 'privacy-policy');
        break;
      }

      case '/terms-of-service': {
        UtilitiesService.importElement(this, 'components/prendus-terms-of-service/prendus-terms-of-service.html', 'terms-of-service');
        break;
      }

      default: break;
    }

  }

  mapStateToThis(e: StatechangeEvent): void {
      const state = e.detail.state;
      this.username = state.currentUser.metaData.email;
      this.loggedIn = this.username ? 'true' : 'false';
      this.mainViewToShow = state.mainViewToShow;
  }
}

Polymer(PrendusRouter);
