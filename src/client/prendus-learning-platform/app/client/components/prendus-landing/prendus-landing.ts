import {Actions} from '../../redux/actions';

class PrendusLanding {
  public is: string;
  public apiKey: string;

  beforeRegister(): void {
    this.is = 'prendus-landing';
  }
  ready(): void {
    this.apiKey = 'AIzaSyB7zQeZeCZFWzwupwYjbioQYldZkdF3oPk';
  }
}
Polymer(PrendusLanding);
