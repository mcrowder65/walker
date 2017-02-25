import {WalkerApp} from '../components/walker-app/walker-app';
import {Marker} from '../typings/marker';

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

const setMarkers = async (context: WalkerApp, markers: Marker[] ): Promise<void> => {
    context.action = {
      type: 'SET_MARKERS',
      markers
    }
}
export const Actions = {
    defaultAction,
    setMarkers
};
