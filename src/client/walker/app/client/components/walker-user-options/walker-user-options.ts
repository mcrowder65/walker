import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {StatechangeEvent} from '../../typings/statechange-event';

export class WalkerUserOptions {
  public is: string;
  public stairs: number;
  public elevation: number;
  public wilderness: boolean = false;
  public building: boolean = false;
  public grass: boolean = false;
  public parkingLots: boolean = false;
  public properties: any;
  public action: Action;

  beforeRegister(): void {
    this.is = 'walker-user-options';
    this.properties = {
      stairs: {
        type: Number,
        observer: 'change'
      },
      elevation: {
        type: Number,
        observer: 'change'
      },
      wilderness: {
        type: Boolean,
        observer: 'change'
      },
      building: {
        type: Boolean,
        observer: 'change'
      },
      grass: {
        type: Boolean,
        observer: 'change'
      },
      parkingLots: {
        type: Boolean,
        observer: 'change'
      }
    }
  }

  change(): void {
    Actions.setStairs(this, this.stairs);
    // Actions.setElevation(this, this.elevation);
    // Actions.setWilderness(this, this.wilderness);
    // Actions.setGrass(this, this.grass);
    // Actions.setBuilding(this, this.building);
    // Actions.setParkingLots(this, this.parkingLots);
  }


  mapStateToThis(e: StatechangeEvent): void {
    const state: State = e.detail.state;
    this.stairs = state.stairs;
    this.elevation = state.elevation;
    this.wilderness = state.wilderness;
    this.grass = state.grass;
    this.building = state.building;
    this.parkingLots = state.parkingLots;
  }
}

Polymer(WalkerUserOptions);
