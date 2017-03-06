import {InitialState} from './initial-state';
import {Actions} from './actions';
import {State} from '../typings/state';
import {Action} from '../typings/action';

export function rootReducer(state: State = InitialState, action: Action): State {
    switch(action.type) {
      case 'SET_MARKERS': {
        return {
          ...state,
          markers: action.markers
        };
      }

      case 'SET_LATITUDE_AND_LONGITUDE': {
        return {
          ...state,
          currentMarker: action.currentMarker
        };
      }

      case 'RESET_MARKER_MODAL': {
        return {
          ...state,
          currentMarker: action.currentMarker
        };
      }

      case 'SET_CURRENT_MARKER': {
        return {
          ...state,
          currentMarker: action.currentMarker
        };
      }

      case 'SET_START_MARKER': {
        return {
          ...state,
          startMarker: action.startMarker
        };
      }

      case 'SET_END_MARKER': {
        return {
          ...state,
          endMarker: action.endMarker
        };
      }
      default: {
          return state;
      }
    }
}
