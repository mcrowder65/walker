import {WalkerMap} from '../components/walker-map/walker-map';
import {Marker} from '../typings/marker';
import {WalkerMarkerModal} from '../components/walker-marker-modal/walker-marker-modal';
import {Options} from '../typings/options';
import {ConstantsService} from '../services/constants-service';

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

const initMarkers = async (context: WalkerMap | WalkerMarkerModal, url: string): Promise<void> => {
  try {
    const response = await POST(url);
    // markers will become an array of marker objects.
    const markers = JSON.parse(response);
    for(let i: number = 0; i < markers.length; i++) {
      markers[i] = JSON.parse(markers[i]);
    }
    setMarkers(context, markers);
  } catch(error) {
    throw error;
  }

};

const setLatitudeAndLongitude = async (context: WalkerMap, currentMarker: Marker): Promise<void> => {
  context.action = {
    type: 'SET_LATITUDE_AND_LONGITUDE',
    currentMarker
  };
}

const resetMarkerModal = async (context: WalkerMarkerModal): Promise<void> => {
  context.action = {
    type: 'RESET_MARKER_MODAL',
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

/**
 * pass data in as a json represented as a string.
 */
const POST = async (url: string, data?: string): Promise<any> => {
  return new Promise(function (resolve, reject) {
      // do the usual Http request
      let request: XMLHttpRequest = new XMLHttpRequest();
      request.open('POST', ConstantsService.baseUrl + url);

      request.onload = function () {
          if (request.status == 200) {
              resolve(request.response);
          } else {
              reject(Error(request.statusText));
          }
      };

      request.onerror = function () {
          reject(Error('Network Error'));
      };

      request.send(data);
  });
};

export const Actions = {
    defaultAction,
    setMarkers,
    setLatitudeAndLongitude,
    initMarkers,
    setCurrentMarker,
    resetMarkerModal,
    POST
};
