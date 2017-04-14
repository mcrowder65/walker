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

      case 'SET_ELEVATION': {
        return {
          ...state,
          elevation: action.elevation
        };
      }

      case 'SET_STAIRS': {
        return {
          ...state,
          stairs: action.stairs
        };
      }

      case 'SET_WILDERNESS': {
        return {
          ...state,
          wilderness: action.wilderness
        };
      }

      case 'SET_GRASS': {
        return {
          ...state,
          grass: action.grass
        };
      }

      case 'SET_BUILDING': {
        return {
          ...state,
          building: action.building
        };
      }

      case 'SET_PARKING_LOTS': {
        return {
          ...state,
          parkingLots: action.parkingLots
        };
      }

      case 'SET_PREFER_DESIGNATED_PATHS': {
        return {
          ...state,
          preferDesignatedPaths: action.preferDesignatedPaths
        };
      }

      case 'SET_DIRECTION_MARKERS': {
        return {
          ...state,
          directionMarkers: action.directionMarkers
        };
      }

      case 'SET_OUT_OF_BOUND_DIRECTIONS': {
        return {
          ...state,
          outOfBoundsDirections: action.outOfBoundsDirections
        };
      }
      default: {
          return state;
      }
    }
}
