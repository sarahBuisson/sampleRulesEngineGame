import React, {Component} from "react";
import PropTypes from 'prop-types';
import map from 'lodash/map';
import styles from "./styles"

class CellComponent extends Component {


    constructor(props) {
        super(props);

    }

    static getDerivedStateFromProps(props, prevState) {
        return {value: props.value, ...prevState}
    }


    handleMouseDown() {
        this.setState({...this.state, holdingClick: true})
    }

    handleMouseUpOnValue(newValue) {
        let state = {...this.state, holdingClick: false, value: this.props.possibilites[newValue]};
        this.setState(state)
    }

    render() {

        if (this.props.initialValue) {
            return <span>{this.props.initialValue}</span>
        } else {
            let possibilitesToolkit;
            if (this.state.holdingClick) {
                possibilitesToolkit = <div style={styles.possibiliteDiv}>{map(this.props.possibilites.concat("X"),
                    (possib, id) => <button key={"buttonPossi" + id}
                                            onMouseUp={() => this.handleMouseUpOnValue(id)}>{possib}</button>)}</div>

            }


            let value = this.state.value ? this.state.value : "";
            return <div onMouseDown={this.handleMouseDown.bind(this)}>
                <input style={styles.inputCase}
                       value={value}/>{possibilitesToolkit}</div>
        }


    }

}

CellComponent.propTypes = {
    value: PropTypes.string,
    initialValue: PropTypes.string,
    possibilites: PropTypes.array,
    onChange: PropTypes.function
};
export default CellComponent;