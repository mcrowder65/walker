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

const setMarkers = async (context: WalkerMap, markers: Marker[] ): Promise<void> => {
    context.action = {
      type: 'SET_MARKERS',
      markers
    }
}

const setMarker = async (context: WalkerMarkerModal, marker: Marker): Promise<void> => {

};
export const Actions = {
    defaultAction,
    setMarkers
};
