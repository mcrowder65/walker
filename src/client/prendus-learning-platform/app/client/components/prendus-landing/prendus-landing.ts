import {Actions} from '../../redux/actions';

class PrendusLanding {
  public is: string;

  beforeRegister() {
    this.is = 'prendus-landing';
  }
}
Polymer(PrendusLanding);
