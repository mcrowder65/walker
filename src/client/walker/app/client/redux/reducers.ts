import {InitialState} from './initial-state';
import {Actions} from './actions';
import {State} from '../typings/state';
import {Action} from '../typings/action';

export function rootReducer(state: State = InitialState, action: Action): State {
    switch(action.type) {
      case 'SET_MARKERS': {
        const newState: State = {
          ...state,
          markers: action.markers
        };
        return newState;
      }

      case 'SET_LATITUDE_AND_LONGITUDE': {
        return {
          ...state,
          currentClickLatitude: action.currentClickLatitude,
          currentClickLongitude: action.currentClickLongitude
        };
      }

      default: {
          return state;
      }
    }
}
