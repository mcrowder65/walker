import {Marker} from '../typings/marker';

const isDefined = (object: Marker | string): boolean => {
  return object !== undefined && object !== null;
}

export const UtilitiesService = {
  isDefined
};
