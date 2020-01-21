import React, {Component} from "react";

import {connect} from "react-redux";

import {bindActionCreators} from "redux";
import {kotlinProxy} from "../../util";
import {newLabObj, newLab, newPartiePointNClick, play, selectObj} from "../../services/actions";

class Labyrinth extends Component {
    constructor() {
        super();
    }

    handleNewGame() {
        this.props.newLabObj(6);
    };

    handleNewLab() {
        this.props.newLab(6);
    };

    renderZone(zone) {
        let zoneJs = kotlinProxy(zone, false);


        return <div style={styles.boardZoneContainer}>
            <div style={styles.boardZoneContent}>
                {zoneJs.contentArray.map((c => c.name))}
            </div>
        </div>
    }


    renderPartie(partie) {

        let partieJs = kotlinProxy(partie, false);


        return kotlinProxy(partieJs.board.contentArray, false).map(it => <div>
            {it.map((cs => this.renderZone(cs)))}

        </div>)

    }


    render() {

        return <div>
            <button onClick={this.handleNewGame}> new Partie</button>
            <button onClick={this.handleNewLab}> new Lab</button>
            <div>{this.props.currentPartie ? this.renderPartie(this.props.curentPartie) : 'no partie'}</div>
        </div>

    }

}


function mapDispatchToProps(dispatch) {
    return {...bindActionCreators({newPartie: newPartiePointNClick, play, newLabObj, selectObj}, dispatch)}
};

function mapStateToProps(state, ownProps) {

    return {
        ...ownProps,
        currentPartie: this.state.currentLabPartie
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Labyrinth)

