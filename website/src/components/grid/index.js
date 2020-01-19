import React, {Component} from "react";
import {kotlinProxy} from "../../util";
import styles from './styles.js'

export class GridComponent extends Component {


    constructor() {
        super();
    }

    render() {
        const {grid} = this.props;

        console.log(kotlinProxy(grid));
        console.log(grid);

        let gridDiv = grid.gridArray.map((item) => {
            let row = item.flatMap((item2) => {
                return <div>x{item2.value}</div>
            })
            return row
        })
        console.log(gridDiv)
        return <div style={styles.grid(grid.gridArray.length)}>{gridDiv}</div>
    }
}
