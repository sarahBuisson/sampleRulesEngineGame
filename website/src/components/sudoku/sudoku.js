import React, {Component} from "react";

export class SudokuApp extends Component {
    constructor() {
        super();
        this.resolve = () => {
        };
        this.newGrid = () => {
        };
    }


    render() {
        return <div key="sudoku"> welcome To the sudoku !


            <button onClick={this.resolve()}>resolve</button>
            <button onClick={this.newGrid()}>newGrid</button>
        </div>
    }
}
