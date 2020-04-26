import React, {Component} from "react";
import {bindActionCreators} from "redux";
import {newSudoku, resoslveGrid, newGridLabObj, newLab, play, selectObj} from "../../services/actions";
import {connect} from "react-redux";
import {kotlinProxy} from "../../util";
import sudoku from 'sudoku'
import map from 'lodash/map';
import CellComponent from "./cell";

import styles from "./styles";

const sudokuSize = [{width: 2, height: 2}, {width: 2, height: 3}, {width: 3, height: 3}, {width: 1, height: 4}];
const sudokuDifficulty = {veryEasy: 2, easy: 3, standard: 4, hard: 5}

let sudokuService = kotlinProxy(sudoku.fr.perso.sudoku.service, true,false);

export class SudokuApp extends Component {
    constructor(props) {
        super(props);
        this.state = {selectedSize:1, selectedDifficulty:"veryEasy"}
    }

    resolve() {
        if (this.state.currentPartie)
            this.setState({...this.state, currentPartie: sudokuService.resolveGrid(this.state.currentPartie)});
    };

    newGrid() {
        let newHeight = sudokuSize[this.state.selectedSize].height;
        let newWidth = sudokuSize[this.state.selectedSize].width;
        let remaingingCase = newWidth * newHeight * newWidth * newHeight / sudokuDifficulty[this.state.selectedDifficulty];
        console.log("should have "+remaingingCase+" given case")
        let currentPartie = sudokuService.generateCleanedGrid(newWidth, newHeight, remaingingCase);
        this.setState({currentPartie});

    };


    renderCell(cell, x, y) {
        let cellStyle = {
            ...styles.gridCase,
            borderLeftStyle: ((x % this.state.currentPartie.height === 0) ? "solid" : "none"),
            borderTopStyle: ((y % this.state.currentPartie.width === 0) ? "solid" : "none"),

            borderRightStyle: ((x === this.state.currentPartie.height*this.state.currentPartie.width-1) ? "solid" : "none"),
            borderBottomStyle: ((y === this.state.currentPartie.height*this.state.currentPartie.width-1) ? "solid" : "none")
        };
        return <div style={cellStyle} key={"cell" + x + "-" + y}>
            <CellComponent value={cell.value} initialValue={cell.value} possibilites={cell.possiblesArray}/></div>
    };

    render() {
        const {currentPartie} = this.state ? this.state : {};

        return <div key="sudoku"> welcome To the sudoku !

            <div>
                size:<select value={this.state.selectedSize}
                onChange={(e) => this.setState({...this.state, selectedSize: e.target.value})}
            >{map(sudokuSize, (data, id) => {
                return <option value={id}>{data.width * data.height}({data.width}:{data.height})</option>

            })}</select>
                difficulty:<select value={this.state.selectedDifficulty}
                onChange={(e) => this.setState({...this.state, selectedDifficulty: e.target.value})}

            >
                {map(sudokuDifficulty, (difficulty, name) => <option value={name}>{name}</option>)}

            </select>
                <button onClick={() => this.newGrid()}>newGrid</button>
                {currentPartie ?
                    <button onClick={() => this.resolve()}>resolve</button> : ""}
            </div>
            <div style={styles.gridContainer}>
                {currentPartie ? this.renderPartie(currentPartie) : "no partie"}
            </div>
            {this.props.currentPartie}
        </div>
    }


    renderPartie(currentPartie) {
        console.log(currentPartie)
        let partieJs = kotlinProxy(currentPartie);

        return map(partieJs.rowsArray, (row, y) => {
            return <div style={styles.gridRowContainer}>{map(row, (cell, x) => {
                return this.renderCell(cell, x, y)
            })}</div>
        });
    }
}

function mapDispatchToProps(dispatch) {
    return {
        ...bindActionCreators({
            newPartie: newSudoku,
            resoslveGrid,
            play,
            newLab,
            newGridLabObj,
            selectObj
        }, dispatch)
    }
};

function mapStateToProps(state, ownProps) {
    console.log("mapStateToProps")
    console.log(state)
    return {
        ...ownProps,
        currentPartie: state.currentSudokuPartie
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(SudokuApp)

