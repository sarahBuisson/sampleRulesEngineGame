export default class CasePossible extends Component {


    render() {
        let {availablePossibles, currentPossibles, value} = this.props

        if (value)
            return <div>{value}</div>
        else {
            let values = availablePossibles.map((p) => <div>{p}</div>)
            return <div>{values}</div>
        }
    }
}
