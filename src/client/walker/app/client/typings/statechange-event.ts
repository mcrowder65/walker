import {State} from './state';

export interface StatechangeEvent extends Event {
    detail: {
        state: State,
        [propName: string]: any
    }
}
