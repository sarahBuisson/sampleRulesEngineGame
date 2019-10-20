import React, {Component} from "react";
export class SudokuApp extends Component{

    resolve=()=>{}
    newGrid=()=>{}



    render(){
        return <div> welcome To the sudoku !



        <button onClick={this.resolve()}>resolve</button>
        <button onClick={this.newGrid()}>newGrid</button>
        </div>
    }
}
