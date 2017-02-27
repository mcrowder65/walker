import {WalkerMap} from '../components/walker-map/walker-map';
import {Marker} from '../typings/marker';
import {WalkerMarkerModal} from '../components/walker-marker-modal/walker-marker-modal';

const defaultAction = (context: any) => {
    context.action = {
        type: 'DEFAULT_ACTION'
    };
};
const showMainSpinner = (context: any) => {
    context.action = {
        type: 'SHOW_MAIN_SPINNER'
    };
};

const setMarkers = async (context: WalkerMap | WalkerMarkerModal, markers: Marker[] ): Promise<void> => {
    context.action = {
      type: 'SET_MARKERS',
      markers
    };
}

const initMarkersWithAjax = async (context: WalkerMap | WalkerMarkerModal, ajax: any): Promise<void> => {
  const request = ajax.generateRequest();
  await request.completes;
  const response = request.response;

  //markers is an array of Marker objects
  const markers = response;
  for(let i: number = 0; i < markers.length; i++) {
    markers[i] = JSON.parse(markers[i]);
  }
  Actions.setMarkers(context, markers);
};

const setLatitudeAndLongitude = async (context: WalkerMap, currentClickLatitude: number, currentClickLongitude: number): Promise<void> => {
  context.action = {
    type: 'SET_LATITUDE_AND_LONGITUDE',
    currentClickLatitude,
    currentClickLongitude
  };
}

const setMarker = async (context: WalkerMarkerModal, marker: Marker): Promise<void> => {

};
export const Actions = {
    defaultAction,
    setMarkers,
    initMarkersWithAjax,
    setLatitudeAndLongitude
};
