import {FirebaseService} from '../node_modules/prendus-services/services/firebase-service';

const defaultAction = (context: any) => {
    context.action = {
        type: 'DEFAULT_ACTION'
    };
};

export const Actions = {
    defaultAction
  };
