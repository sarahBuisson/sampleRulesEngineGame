import React, {Component} from "react";
import {kotlinProxy} from "../../util";
import styles from "./styles";

export class FreeZoneComponent extends Component {


    renderObject(it) {


        let styleObj = {};
        console.log(it);

        if (it.destination) {
            styleObj = {...styles.objectInZone, ...styles.doorInZone};
        } else if (it.want) {
            styleObj = {...styles.objectInZone, ...styles.exchangeInZone};
        } else {
            styleObj = {...styles.objectInZone, ...styles.keyInZone};
        }
        return <div onClick={() => this.props.clickOnObj(it)}
                    style={styleObj}

        >{it.name} {it.destination && it.destination.name}</div>;
    }

    render() {
        const {freeZone} = this.props;
        const freeZoneJs = kotlinProxy(freeZone, false);
        return <div>
            <div>zone {freeZone.name}</div>

            {freeZoneJs.contentArray.map(it => this.renderObject(it))}
        </div>
    }

}


