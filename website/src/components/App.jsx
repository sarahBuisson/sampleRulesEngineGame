import React, {Component} from "react";
import {BrowserRouter as Router, Link, Route, Switch} from "react-router-dom";
import {SudokuApp} from './sudoku/sudoku.js'
import {SkyscraperApp} from "./skyscraper";
import PointNClickApp from "./pointNClick";
import LabyrinthApp from "./labyrinth";
import {D3, D3Force} from "./d3Force";
import Graph from "./d3Force/Graph";

class App extends Component {
    constructor() {
        super();
        this.state = {
            mowers: null
        };
    }


    componentDidMount() {

    }

    render() {
console.log("d3Force")
console.log(D3Force)
        return (<div>myapp
                <Router>
                    <ul>
                        <li>

                            <Link to="/">Home</Link></li>
                        <li><Link to="/littleHorses">Little Horses</Link></li>
                        <li><Link to="/sudoku">sudoku</Link></li>
                        <li><Link to="/skyscraper">skyscraper</Link></li>
                        <li><Link to="/labyrinth">laby</Link></li>
                        <li><Link to="/labyrinthObj">laby && Obj</Link></li>
                        <li><Link to="/pointNClick">Point N click</Link></li>
                        <li><Link to="/d3Force">d3Force</Link></li>
                    </ul>
                    <Switch>
                        <Route exact path="/"
                               render={() => "welcome in the home"}
                        ></Route>
                        <Route exact path="/home"
                               render={() => "welcome in the home"}
                        ></Route>

                        <Route exact path="/sudoku"
                               render={() => <SudokuApp/>}
                        ></Route>

                        <Route exact path="/skyscraper"
                               render={() => <SkyscraperApp/>}
                        ></Route>
                        <Route exact path="/labyrinth"
                               render={() => <LabyrinthApp/>}
                        ></Route>

                        <Route exact path="/labyrinthObj"
                               render={() => <LabyrinthApp/>}
                        ></Route>

                        <Route exact path="/pointNClick"
                               render={() => <PointNClickApp/>}
                        ></Route>
                        <Route exact path="/d3Force"
                               render={() => {
                                   var nodes = [
                                       {"id": "Alice", x:10,y:20},
                                       {"id": "Bob", x:30,y:20},
                                       {"id": "Carol", x:10,y:50}
                                   ];

                                   var links = [
                                       {"source": "Alice", "target": "Bob"},
                                       {"source": "Bob", "target": "Carol"}
                                   ];
                                   return <div><D3/><Graph nodes={nodes} links={links}/></div>}
                               }
                        ></Route>
                    </Switch>
                </Router>
            </div>
        );
    }
}

export default App;
