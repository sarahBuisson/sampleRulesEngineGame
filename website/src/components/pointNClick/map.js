import React, {Component} from "react";
import {kotlinProxy} from "../../util";
import * as d3 from "d3";

/**
 * Map of the freezone
 */
export class MapComponent extends Component {


    componentDidMount() {

        this.force = d3.forceSimulation()
            .force('link', d3.forceLink())
            .force('charge', d3.forceManyBody())
            .force('collide', d3.forceCollide())
           // .force('center', d3.forceCenter(width / 2, height / 2))
            .force("y", d3.forceY(0))
            .force("x", d3.forceX(0));
        this.drawMap();
    }

    drawMap() {
        const {zones} = this.props;
        console.log(zones)
        this.d3Graph = d3.select("#mapD3");
        this.svg = this.d3Graph.append("svg");
        this.svg.attr("width", 700).attr("height", 300).attr("ref", "graph");
        //edit a svg
        this.d3Zone = this.svg.selectAll(".node").data(zones);
        let node = this.d3Zone
            .enter()
            .append("g")
            .attr("transform", (it) => "translate(" + (50 + 600 * Math.random()) + "  " + (50 + 200 * Math.random()) + ")")


        node.append("circle")
            .attr("r", (it) => 10 + 10 * Math.random())
            .attr("fill", "blue");
        node.append("text")
            .text((it) => "zone" + it.name);

        let linkData = zones.map((it)=>it.connectedArray).flat();
        const link =  this.d3Zone.append('g')
            .attr('class', 'links')
            .selectAll('line')
            .data(linkData).enter()
            .append('line');

        const ticked = () => {
            link
                .attr("x1", d => { return d.source.x; })
                .attr("y1", d => { return d.source.y; })
                .attr("x2", d => { return d.target.x; })
                .attr("y2", d => { return d.target.y; });

            node
                .attr("style", d => {
                    return 'left: ' + d.x + 'px; top: ' + (d.y + 72) + 'px';
                });
        };


console.log(zones);


        this.force.nodes(node)
            .on('tick', ticked);

        this.force.force('link')
            .links(linkData);


        //edit an html div
        d3.select("#mapD3").selectAll("div")
            .data(zones)
            .enter().append("div")
            .attr("fill", "red")
            .attr("style", JSON.stringify({
                "position": "absolute",
                "top": Math.random() * 100,
                "left": Math.random() * 100
            }).replace("}", "").replace("{", "").replace(/,/g, ";"))
            .attr("x", Math.random() * 100)
            .attr("y", Math.random() * 100)
            .text((it) => "zone" + it.name);

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
            <br/>{zones.map((it) => <div>{it.name} :{kotlinProxy(it, false).connectedArray.map((c) =>
                <span>{c.name}.</span>)}
            </div>)}
        </div>
    }

    render() {
        return <div></div>
    }





// *****************************************************
// ** d3 functions to manipulate attributes
// *****************************************************

     enterNode (selection) {


        selection.classed('node', true);

        selection.append('text')
            .attr("x", (d) => d.size + 5)
            .attr("dy", ".35em")
            .text((d) => d.key);
    };
    updateNode (selection) {
        selection.attr("transform", (d) => "translate(" + d.x + "," + d.y + ")");
    };
    shouldComponentUpdate(nextProps) {
        const {zones} = this.props;
        //this.d3Zone = this.svg.selectAll(".node").data(zones);

        this.d3Zone.enter().append('g').call(this.enterNode);
        this.d3Zone.exit().remove();
        this.d3Zone.call(this.updateNode);
/*
        var d3Links = this.d3Graph.selectAll('.link')
            .data(nextProps.links, (link) => link.key);
        d3Links.enter().insert('line', '.node').call(enterLink);
        d3Links.exit().remove();
        d3Links.call(updateLink);
*/
        // we should actually clone the nodes and links
        // since we're not supposed to directly mutate
        // props passed in from parent, and d3's force function
        // mutates the nodes and links array directly
        // we're bypassing that here for sake of brevity in example
      //  this.force.nodes(nextProps.nodes)//.links(nextProps.links);
        //this.force.start();

        return false;
    }
}
