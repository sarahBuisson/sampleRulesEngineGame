import React, {Component} from "react";


import sudoku from 'sudoku'
import {kotlinProxy} from "../../util";
import {GridComponent} from "../grid";

export class SkyscraperApp extends Component {


    constructor() {
        super();
        this.props = {}
        this.state = {}
        this.state.current = kotlinProxy(sudoku.fr.perso.skyscraper).generateEmptyGrid(2)

    }

    resolve() {
        if (this.props.current)
            this.setState({current: kotlinProxy(sudoku.fr.perso.skyscraper).resolveGrid(this.props.current)})

    };

    newGrid() {
        this.setState({current: kotlinProxy(sudoku.fr.perso.skyscraper).generateEmptyGrid(5)})
        console.log(this.props.current)

    };


    render() {
        return <div> welcome to the skyscraper !

            <GridComponent grid={this.state.current}/>
            <button onClick={this.resolve}>resolve</button>
            <button onClick={this.newGrid}>newGrid</button>
        </div>
    }
}
