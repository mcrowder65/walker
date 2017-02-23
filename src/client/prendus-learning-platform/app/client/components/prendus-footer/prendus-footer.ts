import {Actions} from '../../redux/actions';

class PrendusFooter {
  public is: string;
  public fire: any;

  beforeRegister(): void {
    this.is = 'prendus-footer';
  }

  getYear(): number {
    const currentDate: Date = new Date();
    return currentDate.getFullYear();
  }

  changeURL(e: any): void {
    const location: string = e.target.id
    window.history.pushState({}, '', location);
    this.fire('location-changed', {}, {node: window});
  }
}

Polymer(PrendusFooter);
