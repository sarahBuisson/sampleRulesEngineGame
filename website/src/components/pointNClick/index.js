import React, {Component} from "react";
import {kotlinProxy} from "../../util";
import {FreeZoneComponent as FreeZone} from "./FreeZone"


import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import {newLab, newPartiePointNClick, play, selectObj} from '../../services/actions'
import styles from './styles.js'
import {MapComponent} from "./map";
import {MapNew} from "./mapNew";

class PointNClickApp extends Component {

    constructor(props={}) {
        super(props);
        this.state = {};

    }

    newGame() {
        this.props.newPartie(6);
    };

    newLab(){
        this.props.newLab(6);
    };

    selectObj(obj) {
        this.props.selectObj(obj);
    };


    clickOnObj(obj) {
console.log("pnc")
        console.log(obj)
        console.log(this.props.currentPartie)
        this.props.play(this.props.currentPartie, obj)
    };


    renderPartie(partie) {
        let partieJs = kotlinProxy(partie, false);
        let playerJs = kotlinProxy(partieJs.player, false);
        return <div>

            <div>
                <FreeZone freeZone={partieJs.player.location} partie={partie} clickOnObj={(partie,obj)=>this.clickOnObj(partie,obj)}/>
            </div>
            <div>
                selected: {playerJs.selected ? playerJs.selected.name : ''}

            </div>
            <div>
                <div>inventory:</div>
                {playerJs.inventoryArray.map(it => {
                    return <div style={styles.objectInInventory} onClick={() => this.selectObj(it)}>{it.name}</div>
                })}</div>
            <div>
                <MapNew zones={partieJs.levelArray}/>
            </div>
        </div>
    };

    render() {

        const partie = this.props.currentPartie;
        let content;
        if (partie) {
            console.log("render partie ")
            console.log(partie)
            content = this.renderPartie(partie)
        } else {
            content = "no partie loaded";
        }
        return <div>
            <div>welcome to the labyrinth !</div>
            {content}
            <br/>

            <button onClick={this.newLab.bind(this)}>new Lab</button>
            <button onClick={this.newGame.bind(this)}>new Partie</button>
        </div>

        /*
        *
           */
    }
}


function mapDispatchToProps(dispatch) {
    return {...bindActionCreators({newPartie: newPartiePointNClick, play, newLab, selectObj}, dispatch)}
};

function mapStateToProps(state, ownProps) {

    return {
        ...ownProps,
        currentPartie: state.currentPartie
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(PointNClickApp)

