import React, {Component} from "react";
import {kotlinProxy} from "../../util";
import * as d3 from "d3";
/**
 * Map of the freezone
 */
export class MapComponent extends Component {
    render() {


        const {zones} = this.props;

        const svg = d3.select("#mapD3").append("svg").attr("width", 700).attr("height", 300);




        svg.selectAll("circle").data(zones).enter().append("circle").attr("cx",10).attr("cy",10).attr("r",10).attr("fill","blue").text((it)=>it.name);
        d3.select("#mapD3").selectAll("div").data(zones).enter().append("div").attr("fill","blue").text((it)=>it.name);
/*
        zones.forEach((it) =>kotlinProxy(it,false).connectedArray.forEach((c) =>
            {
                svg.append("p").text(it.name)
            }
        ));
*/
        console.log(zones)
        return <div>Map:<br/>
                <div id="mapD3"></div>
        <br/>{zones.map((it) => <div>{it.name} :{kotlinProxy(it,false).connectedArray.map((c) => <span>{c.name}.</span>)}</div>)} </div>
    }
}
