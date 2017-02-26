import {InitialState} from './initial-state';
import {Actions} from './actions';
import {State} from '../typings/state';
import {Action} from '../typings/action';

export function rootReducer(state: State = InitialState, action: Action): State {
    switch(action.type) {
      case 'SET_MARKERS': {
        console.log('SET_MARKERS');
        const newState: State = {
          ...state,
          markers: action.markers
        };
        console.log(newState);
        return newState;
      }

      case 'SET_LATITUDE_AND_LONGITUDE': {
        console.log(action.currentClickLatitude + ' ' + action.currentClickLongitude)
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
