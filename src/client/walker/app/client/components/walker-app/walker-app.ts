import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {Marker} from '../../typings/marker';

export class WalkerApp {
  public is: string;
  public username: string;
  public rootReducer: (state: State, action: Action) => State;
  public querySelector: any;
  public markers: Marker[];
  public action: Action;

  beforeRegister(): void {
    this.is = 'walker-app';
  }

  ready(): void {
    this.rootReducer = rootReducer;
  }

  async initMarkers(): Promise<void> {
    const ajax = this.querySelector('#handler');
    const request = ajax.generateRequest();
    await request.completes;
    const response = request.response;

    //markers is an array of Marker objects
    const markers = response;

    for(let i: number = 0; i < markers.length; i++) {
      markers[i] = JSON.parse(markers[i]);
    }
    Actions.setMarkers(this, markers);
  }

  async clearMarkers(): Promise<void> {
    Actions.setMarkers(this, []);
  }

  async mapClicked(e: any): Promise<void> {
    const latitude: number = e.detail.latLng.lat();
    const longitude: number = e.detail.latLng.lng();
  }

  mapStateToThis(e: any): void {
    const state: State = e.detail.state
    this.markers = state.markers;
  }

}

Polymer(WalkerApp);
