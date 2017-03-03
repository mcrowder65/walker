import {WalkerMap} from '../components/walker-map/walker-map';
import {Marker} from '../typings/marker';
import {WalkerMarkerModal} from '../components/walker-marker-modal/walker-marker-modal';
import {Options} from '../typings/options';

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
  try {
    const request = ajax.generateRequest();
    await request.completes;
    const response = request.response;

    // markers is an array of Marker objects
    const markers = response;
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

/**
 * this set marker hits the server which sets the marker in firebase.
 */
const setMarker = async (marker: Marker, ajax: any): Promise<void> => {
  try {
    ajax.body = {
      latitude: marker.latitude,
      longitude: marker.longitude,
      title: marker.title,
      id: marker.id,
      openingTime: marker.openingTime,
      closingTime: marker.closingTime
    };

    const request = ajax.generateRequest();
    await request.completes;
  } catch(error) {
    throw error;
  }

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
  try {
    ajax.body = {
      id: marker.id,
      longitude: marker.longitude,
      latitude: marker.latitude,
      title: marker.title,
      openingTime: marker.openingTime,
      closingTime: marker.closingTime
    };

    const request = ajax.generateRequest();
    await request.completes;
  } catch(error) {
    throw error;
  }
};

const ajax = async (options: Options): Promise<any> => {
  let xhr: XMLHttpRequest = new XMLHttpRequest();
  xhr.open(options.method, options.url, false );
  await xhr.send(JSON.stringify(options.body));
  const response = await xhr.response
  console.log('response ', response);
};

function httpGet(url) {
    return new Promise(function (resolve, reject) {
        // do the usual Http request
        let request = new XMLHttpRequest();
        request.open('GET', url);

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

        request.send();
    });
}

async function httpGetJson(url) {
    // check if the URL looks like a JSON file and call httpGet.
    let regex = /\.(json)$/i;

    if (regex.test(url)) {
        // call the async function, wait for the result
        return await httpGet(url);
    } else {
        throw Error('Bad Url Format');
    }
}
export const Actions = {
    defaultAction,
    setMarkers,
    setLatitudeAndLongitude,
    initMarkersWithAjax,
    setMarker,
    setCurrentMarker,
    resetMarkerModal,
    deleteMarker,
    ajax
};
