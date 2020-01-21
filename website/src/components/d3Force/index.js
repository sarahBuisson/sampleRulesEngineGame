import * as d3 from 'd3';
import React, {createRef, useRef, Component} from "react";


export function D3(props) {
    const ref = useRef(null)
    const svg = d3.select(ref.current)
    svg.selectAll("circle").data([1, 2, 3]).enter().append('circle').attr('r', 10).append("div").text((it) => {

        console.log(it);
        return it.name
    });
    return <svg
        className="d3-component"
        width={400}
        height={200}
        ref={ref}
    />


}

export class D3Force extends Component {

    constructor(props) {
        super(props);/*
        console.log(props)
        const ref = useRef(null)
        this.svg = d3.select(ref.current)
        this.force = d3.forceSimulation(props.nodes)
            .force("charge", d3.forceManyBody())
            .force("link", d3.forceLink().id(function (d) {
                return d.id;
            }))
            .force("x", d3.forceX(this.props.width / 2))
            .force("y", d3.forceY(this.props.height / 2));
        d3.forceLink(props.links);*/
    }

    componentDidMount() {
        this.force.on('tick', () => {
            // after force calculation starts, call
            // forceUpdate on the React component on each tick
            this.forceUpdate()
        });
    }

    render() {
        /*
                const ref = createRef(null)
                const svg = d3.select(ref.current)
                svg.selectAll("circle").data([1, 2, 3]).enter().append('circle').attr('r', 10).append("div").text((it) => {

                    console.log(it);
                    return it.name
                });
                return <div>SVG
                    <svg
                        className="d3-component"
                        width={400}
                        height={200}
                        ref={ref}
                    />
                </div>
        */
        return <div>sss</div>
    }

}


const styles = {
    svg: {
        width: "100%",
        height: "100%",
    }
}


export class Graph extends Component {
    constructor() {
        super();
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
            console.log("tick")
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
            .force("link", d3.forceLink().id(function (d) {
                return d.id;
            }).distance(60))
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

        console.log("mount")
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
            .data(data.nodes, d => d.id)

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
        console.log("data loaded")
    }

    render() {
        let dataBasic = {nodes: this.props.nodes, links: this.props.links};
        console.log(dataBasic)
        return (
            <div id="wrapper" ref="wrapper" style={styles.svg}>
                <button onClick={() => this.loadData(dataBasic)}>
                    To data Basic
                </button>
                <button onClick={() => this.loadData(dataReduced)}>
                    To data Reduced
                </button>
                <button onClick={() => this.loadData(dataExtended)}>
                    To data Extended
                </button>
                <svg id="svg-id" ref="svg-ref" style={styles.svg}></svg>
            </div>
        )
    }
}
