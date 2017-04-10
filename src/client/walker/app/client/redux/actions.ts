import {WalkerMap} from '../components/walker-map/walker-map';
import {WalkerUserOptions} from '../components/walker-user-options/walker-user-options';
import {Marker} from '../typings/marker';
import {WalkerMarkerModal} from '../components/walker-marker-modal/walker-marker-modal';
import {Options} from '../typings/options';
import {ConstantsService} from '../services/constants-service';
import {UserOptions} from '../typings/user-options';

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

const setMarkers = (context: WalkerMap | WalkerMarkerModal, markers: Marker[]): void => {
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
    context.errorMessage = '';
    context.errorMessage = 'The server is probably not on.';
    throw error;
  }

};

const setLatitudeAndLongitude = (context: WalkerMap, currentMarker: Marker): void => {
  context.action = {
    type: 'SET_LATITUDE_AND_LONGITUDE',
    currentMarker
  };
}

const resetMarkerModal = async (context: WalkerMarkerModal): Promise<void> => {
  context.action = {
    type: 'RESET_MARKER_MODAL'
  }
};

const setStartMarker = (context: WalkerMap, startMarker: Marker): void => {
  context.action = {
    type: 'SET_START_MARKER',
    startMarker
  }
};

const setEndMarker = (context: WalkerMap, endMarker: Marker): void => {
  context.action = {
    type: 'SET_END_MARKER',
    endMarker
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

const travel = async (context: WalkerMap, url: string, startMarker: Marker, endMarker: Marker, userOptions: UserOptions): Promise<any> => {
    try {
      const body = {
        startMarker,
        endMarker,
        userOptions
      };
      const response = await POST(url, JSON.stringify(body));
      const directionMarkers: Marker[] = JSON.parse(response);
      context.action = {
        type: 'SET_DIRECTION_MARKERS',
        directionMarkers
      }
    } catch(error) {
      throw error;
    }
};

const setStairs = (context: any, stairs: number): void => {
  context.action = {
    type: 'SET_STAIRS',
    stairs
  };
}

const setElevation = (context: WalkerUserOptions, elevation: number): void => {
  context.action = {
    type: 'SET_ELEVATION',
    elevation
  };
}

const setWilderness = (context: WalkerUserOptions, wilderness: number): void => {
  context.action = {
    type: 'SET_WILDERNESS',
    wilderness
  };
}

const setGrass = (context: WalkerUserOptions, grass: number): void => {
  context.action = {
    type: 'SET_GRASS',
    grass
  };
}

const setBuilding = (context: WalkerUserOptions, building: number): void => {
  context.action = {
    type: 'SET_BUILDING',
    building
  };
}

const setParkingLots = (context: WalkerUserOptions, parkingLots: number): void => {
  context.action = {
    type: 'SET_PARKING_LOTS',
    parkingLots
  }

}

const setPreferDesignatedPaths = (context: WalkerUserOptions, preferDesignatedPaths: number): void => {
  context.action = {
    type: 'SET_PREFER_DESIGNATED_PATHS',
    preferDesignatedPaths
  };
}
export const Actions = {
    defaultAction,
    setMarkers,
    setLatitudeAndLongitude,
    initMarkers,
    setStartMarker,
    setEndMarker,
    setCurrentMarker,
    resetMarkerModal,
    POST,
    travel,
    setStairs,
    setElevation,
    setWilderness,
    setGrass,
    setBuilding,
    setParkingLots,
    setPreferDesignatedPaths
};
