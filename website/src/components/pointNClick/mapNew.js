import React, {Component} from "react";
import {kotlinProxy} from "../../util";
import * as d3 from "d3";

export class MapNew extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        const url = 'https://raw.githubusercontent.com/DealPete/forceDirected/master/countries.json';


        const zonesData = this.props.zones;
        const linksData = this.props.zones.map((zone, indexZ) => {
                let map = kotlinProxy(zone, false).connectedArray.map((con) => {
                    let target = zonesData.findIndex(it => it.name == con.name);

                    let newVar = {"target": target, "source": indexZ};

                    return newVar
                });

                return map
            }
        ).flat();
        const width = 640,
            height = 480;

        //Initializing chart
        const chart = d3.select('.chart')
            .attr('width', width)
            .attr('height', height);

        //Creating tooltip
        const tooltip = d3.select('.container')
            .append('div')
            .attr('class', 'tooltip')
            .html('Tooltip');

        //Initializing force simulation
        const simulation = d3.forceSimulation()
            .force('link', d3.forceLink().distance(150))
            .force('charge', d3.forceManyBody())
            .force('collide', d3.forceCollide())
            .force('center', d3.forceCenter(width / 2, height / 2))
            .force("y", d3.forceY(10))
            .force("x", d3.forceX(10));


        //Drag functions
        const dragStart = d => {
            if (!d3.event.active) simulation.alphaTarget(0.3).restart();
            d.fx = d.x;
            d.fy = d.y;
        };

        const drag = d => {
            d.fx = d3.event.x;
            d.fy = d3.event.y;
        };

        const dragEnd = d => {
            if (!d3.event.active) simulation.alphaTarget(0);
            d.fx = null;
            d.fy = null;
        }

        //Creating links
        const link = chart.append('g')
            .attr('class', 'links')
            .selectAll('line')
            .data(linksData).enter()
            .append('line')
            .attr("stroke","blue");

        //Creating nodes
        const node = chart.append('g')
            .attr('class', 'nodes')
            .selectAll('g')
            .data(zonesData).enter()
            .append("g");

            node.append('circle')
                .attr("r",20+10* Math.random())
                .attr("cx",20)
                .attr("fill","grey")


            node.call(d3.drag()
                .on('start', dragStart)
                .on('drag', drag)
                .on('end', dragEnd)
            ).on('mouseover', d => {
                tooltip.html(d.country)
                    .style('left', d3.event.pageX + 5 + 'px')
                    .style('top', d3.event.pageY + 5 + 'px')
                    .style('opacity', .9);
            }).on('mouseout', () => {
                tooltip.style('opacity', 0)
                    .style('left', '0px')
                    .style('top', '0px');
            });
        node.append("text").text((it) => "zone" + it.name)
        //Setting location when ticked
        const ticked = () => {
            link
                .attr("x1", d => {
                    return d.source.x;
                })
                .attr("y1", d => {
                    return d.source.y;
                })
                .attr("x2", d => {
                    return d.target.x;
                })
                .attr("y2", d => {
                    return d.target.y;
                });

            node

                .attr("transform",  d => {return `translate(${d.x} ${d.y} )`});
        };

        //Starting simulation
        simulation.nodes(zonesData)
            .on('tick', ticked);

        simulation.force('link')
            .links(linksData);


    }

    render() {
        return (
            <div className='container'>
                <h1>Map</h1>
                <div className='chartContainer'>
                    <svg className='chart'>
                    </svg>
                </div>
            </div>
        );
    }
}
