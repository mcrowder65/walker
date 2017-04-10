import {State} from '../../typings/state';
import {Action} from '../../typings/action';
import {Actions} from '../../redux/actions';
import {StatechangeEvent} from '../../typings/statechange-event';

export class WalkerUserOptions {
  public is: string;
  public stairs: number;
  public elevation: number;
  public wilderness: number;
  public building: number;
  public grass: number;
  public parkingLots: number;
  public preferDesignatedPaths: number;
  public properties: any;
  public action: Action;
  public querySelector: any;

  beforeRegister(): void {
    this.is = 'walker-user-options';
  }

  change(): void {
    if(this.querySelector('#stairs')) {
      Actions.setStairs(this, this.querySelector('#stairs').value);
    }
    if(this.querySelector('#elevation')) {
      Actions.setElevation(this, this.querySelector('#elevation').value);
    }
    if(this.querySelector('#wilderness')) {
      Actions.setWilderness(this, this.querySelector('#wilderness').value);
    }
    if(this.querySelector('#grass')) {
      Actions.setGrass(this, this.querySelector('#grass').value);
    }
    if(this.querySelector('#building')) {
      Actions.setBuilding(this, this.querySelector('#building').value);
    }
    if(this.querySelector('#parkingLots')) {
      Actions.setParkingLots(this, this.querySelector('#parkingLots').value);
    }
    if(this.querySelector('#preferDesignatedPaths')) {
      Actions.setPreferDesignatedPaths(this, this.querySelector('#preferDesignatedPaths').value);
    }
  }

  mapStateToThis(e: StatechangeEvent): void {
    const state: State = e.detail.state;
    this.stairs = state.stairs;
    this.elevation = state.elevation;
    this.wilderness = state.wilderness;
    this.grass = state.grass;
    this.building = state.building;
    this.parkingLots = state.parkingLots;
    this.preferDesignatedPaths = state.preferDesignatedPaths;
  }
}

Polymer(WalkerUserOptions);
