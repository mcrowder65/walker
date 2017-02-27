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

  // markers is an array of Marker objects
  const markers = response;
  for(let i: number = 0; i < markers.length; i++) {
    markers[i] = JSON.parse(markers[i]);
  }
  setMarkers(context, markers);
};

const setLatitudeAndLongitude = async (context: WalkerMap, currentMarker: Marker): Promise<void> => {
  context.action = {
    type: 'SET_LATITUDE_AND_LONGITUDE',
    currentMarker
  };
}

/**
 * this set marker hits the server which sets the marker in firebase.
 */
const setMarker = async (marker: Marker, ajax: any): Promise<void> => {
  ajax.body = {
    latitude: marker.latitude,
    longitude: marker.longitude,
    title: marker.title,
    id: marker.id
  };

  const request = ajax.generateRequest();
  await request.completes;
};

const resetMarkerModal = async (context: WalkerMarkerModal): Promise<void> => {
  context.action = {
    type: 'RESET_MARKER_MODAL'
  }
};

/**
 * This set marker just sets the current marker in the state for the marker modal.
 */
const setCurrentMarker = async (context: WalkerMap, currentMarker: Marker): Promise<void> => {
  context.action = {
    type: 'SET_CURRENT_MARKER',
    currentMarker
  };
};

const deleteMarker = async (marker: Marker, ajax: any): Promise<void> => {
  ajax.body = {
    id: marker.id,
    longitude: marker.longitude,
    latitude: marker.latitude,
    title: marker.title
  };

  const request = ajax.generateRequest();
  await request.completes;

  
};

export const Actions = {
    defaultAction,
    setMarkers,
    setLatitudeAndLongitude,
    initMarkersWithAjax,
    setMarker,
    setCurrentMarker,
    resetMarkerModal,
    deleteMarker
};
