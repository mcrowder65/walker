import {rootReducer} from '../../redux/reducers';
import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {Marker} from '../../typings/marker';

export class WalkerMap {
  public is: string;
  public username: string;
  public rootReducer: (state: State, action: Action) => State;
  public querySelector: any;
  public markers: Marker[];
  public action: Action;

  beforeRegister(): void {
    this.is = 'walker-map';
  }

  ready(): void {
    this.rootReducer = rootReducer;
  }

  async initMarkers(): Promise<void> {
    const ajax = this.querySelector('#getMarkersAjax');
    const request = ajax.generateRequest();
    await request.completes;
    const response = request.response;

    //markers is an array of Marker objects
    const markers = response;

    for(let i: number = 0; i < markers.length; i++) {
      markers[i] = JSON.parse(markers[i]);
    }
    console.log(markers);
    Actions.setMarkers(this, markers);
  }

  async setMarker(marker: Marker): Promise<void> {
    const ajax = this.querySelector('#setMarkerAjax');

    ajax.body = {
      latitude: marker.latitude,
      longitude: marker.longitude,
      title: marker.title
    };
    const request = ajax.generateRequest();
    await request.completes;
    const response = request.response;
    console.log(response);
  }

  async clearMarkers(): Promise<void> {
    Actions.setMarkers(this, []);
  }

  async mapClicked(e: any): Promise<void> {
    const latitude: number = e.detail.latLng.lat();
    const longitude: number = e.detail.latLng.lng();

    Actions.setLatitudeAndLongitude(this, latitude, longitude);

    this.querySelector('#walker-marker-modal').open();

  }

  mapStateToThis(e: any): void {
    const state: State = e.detail.state
    this.markers = state.markers;
    console.log('mapStateToThis walker-map');
    console.log(this.markers);
  }

}

Polymer(WalkerMap);
