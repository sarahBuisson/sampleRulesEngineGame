import React, {Component} from 'react';

import * as d3 from "d3";

// https://medium.com/@bryony_17728/d3-js-merge-in-depth-a3069749a84f
// https://bl.ocks.org/almsuarez/baa897c189ed64ba2bb32cde2876533b
// http://bl.ocks.org/eyaler/10586116
// https://medium.com/ninjaconcept/interactive-dynamic-force-directed-graphs-with-d3-da720c6d7811

// https://d3indepth.com/enterexit/
// https://d3indepth.com/selections/

const styles = {
    svg: {
        width: "100%",
        height: "100%",
    }
}


export const dataBasic = {
    nodes:
        [
            {
                uuid: "0",
                lab: "alpha",
                name: "alpha",
            },
            {
                uuid: "1",
                lab: "beta",
                name: "beta",
            },
            {
                uuid: "2",
                lab: "gamma",
                name: "gamma",
            },
            {
                uuid: "3",
                lab: "delta",
                name: "delta",
            },
            {
                uuid: "4",
                lab: "epsilon",
                name: "espilon",
            },
            {
                uuid: "5",
                lab: "zeta",
                name: "zeta",
            },
        ],
    links: [
        {
            source: "0",
            target: "1",
        },
        {
            source: "0",
            target: "5",
        },
        {
            source: "5",
            target: "3",
        },
        {
            source: "0",
            target: "3",
        },
        {
            source: "2",
            target: "1",
        },
        {
            source: "4",
            target: "1",
        },
    ]
}


class Graph extends Component {
    constructor(props) {
        super(props);
        this.setState({
            width: 600,
            height: 600,
        })

    }


    // Set up the simulation, needed only once.
    componentDidMount() {
        // wrapper is the div in which the simulation will run.
        // We want the simulation to fill all the wrapper
        const height = document.getElementById('wrapper').clientHeight
        const width = document.getElementById('wrapper').clientWidth
        this.setState({width: width, height: height})

        // A tick function for the simulation. The nodes will be group so we use
        // a transform
        const ticked = () => {
            this.links
                .attr('x1', link => link.source.x)
                .attr('y1', link => link.source.y)
                .attr('x2', link => link.target.x)
                .attr('y2', link => link.target.y)

            this.nodes
                .attr('transform', d => `translate(${d.x}, ${d.y})`)
        }

        // Let's select the svg component
        this.graph = d3.select(this.refs["svg-ref"])

        // This is the classic way of building a simulation.
        this.simulation = d3.forceSimulation()
            .force("link", d3.forceLink().id(d => d.uuid).distance(60))
            .force('charge', d3.forceManyBody().strength(-30))
            .force('center', d3.forceCenter(this.state.width / 2, this.state.height / 2))
            .on("tick", ticked);

        // We build a group for links
        this.links = this.graph
            .append('g')
            .attr("class", "links")
            .selectAll('line')

        // We build a group for nodes
        this.nodes = this.graph
            .append('g')
            .attr("class", "nodes")
            .selectAll("g")


    }

    // This function loads the data in the simulation
    loadData(data) {

        // Add new data to the group of links, with keys
        this.links = this.links
            .data(data.links, d => d.source + "-" + d.target)

        // Remove outdated data
        this.links.exit().remove()

        // Associate svg elements to the data and merge the group of links
        this.links = this.links
            .enter()
            .append('line')
            .attr('stroke-width', 1)
            .attr('stroke', '#808080')
            .merge(this.links)

        // Add new data to the group of nodes, with keys
        this.nodes = this.nodes
            .data(data.nodes, d => d.uuid)

        // Remove outdated data
        this.nodes.exit().remove()

        // Here we got to handle the fact that nodes are in group and that we want
        // to add several svg elements to each group
        // First let's get all our new nodes and ad groups
        this.newnodes = this.nodes
            .enter()
            .append('g')
            .attr("class", "nodesGroups")

        // We can immediately merge them, the point was just to keep e reference to
        // the new elements
        this.nodes = this.newnodes.merge(this.nodes)

        // We add everything we want to the new elements (the new nodes as groups)
        // We add circle
        this.newnodes
            .append("circle")
            .attr("r", 5)
        // We add labels
        this.newnodes
            .append("text")
            .text(d => d.name)
            .style("font-size", "13px")
            .style("font-family", "Roboto")
            .attr("dx", "1em")
            .attr("dy", "0.3em")

        // Add our groups of nodes and links to the simulation and restart it
        this.simulation.nodes(data.nodes)
        this.simulation.force("link").links(data.links);
        this.simulation.alpha(0.8).restart()
    };

    render() {
        return (
            <div id="wrapper" ref="wrapper" style={styles.svg}>
                <button onClick={() => this.loadData(dataBasic)}>
                    To data Basic
                </button>
                <button onClick={() => this.loadData(dataBasic)}>
                    To data Reduced
                </button>
                <button onClick={() => this.loadData(dataBasic)}>
                    To data Extended
                </button>
                <svg id="svg-id" ref="svg-ref" style={styles.svg}></svg>
            </div>
        )
    }
}

export default Graph;
