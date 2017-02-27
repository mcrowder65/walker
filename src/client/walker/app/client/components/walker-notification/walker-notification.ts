import {Actions} from '../../redux/actions';
import {StatechangeEvent} from '../../typings/statechange-event';
class WalkerNotification {
  public is: string;
  public username: string;
  public properties: any;
  public toastText: string;
  public errorMessage: string;
  public successMessage: string;
  public querySelector: any;

  beforeRegister(): void {
    this.is = 'walker-notification';
    this.properties = {
        successMessage: {
          type: String,
          observer: 'showSuccessMessage'
        },
        errorMessage: {
          type: String,
          observer: 'showErrorMessage'
        }
    }
  }

  showErrorMessage(): void {
    if(this.errorMessage) {
      this.toastText = this.errorMessage;
      this.querySelector('#errorToastContainer').open();
    }
  }

  showSuccessMessage(): void {
    if(this.successMessage) {
      this.toastText = this.successMessage;
      this.querySelector('#successToastContainer').open();
    }
  }
}

Polymer(WalkerNotification);
